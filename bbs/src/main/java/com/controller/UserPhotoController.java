package com.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.db.MyBatis;
import com.db.User;
import com.util.FileStore;
import com.util.MyUtil;
import com.util.TmpFile;
import net.coobird.thumbnailator.Thumbnails;
import xspring.RestData;

@Controller
public class UserPhotoController
{
	public static FileStore store = new FileStore("C:/bbsfile/photo/", "/bbsfile/photo/");
	
	@PostMapping("/u/user/photoUp.do")
	public Object photoUp(HttpServletRequest request,
			@SessionAttribute User user) throws Exception
	{
		MultipartHttpServletRequest mhr = (MultipartHttpServletRequest) request;
		
		MultipartFile mf = mhr.getFile("file");	//表单里的 name='file'
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(mf != null && !mf.isEmpty())
		{
			//取得上传上来的文件名
			String realName = mf.getOriginalFilename();
			//获得后缀名
			String suffix = MyUtil.getSuffix(realName);
			//改文件名为随机数字和后缀名的拼接
			String tmpName = MyUtil.guid2() + suffix;
			
			//获得临时文件路径
			File tmpFile = FileStore.tmpStore.getFile( tmpName);
			
			//接收上传
			mf.transferTo(tmpFile);
			System.out.println("** file: " + tmpFile.getAbsolutePath());
			
			if(tmpFile.length() > 1000000)
				throw new Exception("图片太大！需小于1M！");
			
			//生成正式的URL
			String url = usePhoto(user, tmpFile);
			
			result.put("realName", realName);
			result.put("tmpName", tmpName);
			result.put("tmpUrl", FileStore.tmpStore.getUrl( tmpName));
			result.put("url", url);
		}
		
		return new RestData(result);
	}
	
	//保存头像
	public String usePhoto(User user, File tmpFile) throws Exception
	{
		//构造路径
		String path = String.format("%03d/%d_%s.jpg", 
				user.id / 1000,
				user.id,
				MyUtil.guid2());
		
		//删除原先的头像文件
		if(user.thumb != null && user.thumb.length() > 0)
		{
			try {
				FileUtils.deleteQuietly(store.fileOfUrl(user.thumb));
			}catch(Exception e) {
				System.out.println("删除文件失败:" + e.getMessage());
			}
		}
		
		//保存新的头像图片
		File dstFile = store.getFile(path);
		dstFile.getParentFile().mkdirs(); //创建层次目录
		
		//FileUtils.moveFile(tmpFile, dstFile);
		//剪裁图片
		clipPhoto(tmpFile, dstFile);
		
		String url = store.getUrl(path);
		user.thumb = url;
		
		try(SqlSession db = MyBatis.factory.openSession())
		{
			db.update("mapper.user.photo", user);
			db.commit(true);
		}
		
		return url;
	}
	
	//图片裁剪（100*100）
	public void clipPhoto(File srcFile, File dstFile) throws Exception
	{
		//读取原图，缩小为100*100像素
		BufferedImage img = Thumbnails.of(srcFile)
				.size(100, 100)
				.asBufferedImage();
		
		//截取中间的正方形
		int w = img.getWidth();
		int h = img.getHeight();
		int size = w < h ? w : h;
		int x = (w-size)/2;
		int y = (h-size)/2;
		
		//截取
		Thumbnails.of(img)
			.scale(1.0f)
			.sourceRegion(x, y, size, size)
			.outputFormat("jpg")
			.toFile(dstFile);
	}
}
