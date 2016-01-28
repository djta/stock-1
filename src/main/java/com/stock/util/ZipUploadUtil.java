package com.stock.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.fileServer.qiniu.FileServerQiNiu;

@Component
public class ZipUploadUtil {

	private static FileServerQiNiu qiniu;
	
	@Autowired
	private void setQiniu(FileServerQiNiu qiniu) {
		ZipUploadUtil.qiniu = qiniu;
	}
	
	public static void zipUpload(String folderPath) {
		String zipPath = ZipUtil.zip(folderPath).getPath();
		qiniu.upload(zipPath);
	}
}
