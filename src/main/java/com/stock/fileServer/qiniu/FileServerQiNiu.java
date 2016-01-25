package com.stock.fileServer.qiniu;

import java.io.IOException;

import javax.annotation.PostConstruct;

import lombok.NoArgsConstructor;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

@Component
@NoArgsConstructor
public class FileServerQiNiu {
	
	private static final String FILE_URL_FORMAT = "http://%s/%s";
	
	@Autowired
	private PathMatchingResourcePatternResolver resolver;
	
	@Value("${com.qiniu.domain}")
	private String domain;
	
	@Value("${com.qiniu.bucket}")
	private String bucket = "jzhang";

	@Value("${com.qiniu.access_key}")
	private String accessKey;
	
	@Value("${com.qiniu.secret_key}")
	private String secretKey;
	
	private Auth auth;
	
	private UploadManager uploadManager;
	
	private BucketManager bucketManager;
	
	private OperationManager operater;
	
	@PostConstruct
	private void init(){
		auth = Auth.create(accessKey, secretKey);
		uploadManager = new UploadManager();
		bucketManager = new BucketManager(auth);
		operater = new OperationManager(auth);
	}
	
	public void upload(String filePath) {
		try {
			Resource fres = new FileSystemResource(filePath);
			byte[] bytResource = IOUtils.toByteArray(fres.getInputStream());
			upload(bytResource, fres.getFilename());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String upload(byte[] byteOrFile, String fileName) {
	    try {
	    	String upToken = getUpToken();
	        Response res = uploadManager.put(byteOrFile, fileName, upToken);
	        if (res.isOK()) {
	        	MyRet myret = res.jsonToObject(MyRet.class);
	        	return getUrl(myret.key);
	        }
	    } catch (QiniuException e) {
	       e.printStackTrace();
	    }
	    
	    return null;
	}
	
	public String fetch(String url) {
		try {
			String key = bucketManager.fetch(url, bucket).key;
			return getUrl(key);
		} catch (QiniuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String amr2map3(String key) {
		String newKey = key + ".mp3";
		pfop(key, newKey, "avthumb/mp3/acodec/libmp3lame/stripmeta/1");
//		JSONObject jsonObj = wxApiHelper.getQiniuFileDetail(id);
//		String mp3Key = jsonObj.getJSONArray("items").getJSONObject(0).get("key").toString();
		return getUrl(newKey);
	}
	
	public String pfop(String key, String newKey, String operations) {
		StringMap params = new StringMap().putNotEmpty("notifyURL", "")
				.putWhen("force", 1, true).putNotEmpty("pipeline", "");
		String fops = operations + "|saveas/" + UrlSafeBase64.encodeToString(bucket + ":" + newKey);
		try {
			String id = operater.pfop(bucket, key, fops, params);
			return id;
		} catch (QiniuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/** Private function start *****************************/
	
	private String getUpToken(){
	    return auth.uploadToken(bucket, null, 3600, new StringMap()
	            .putNotEmpty("returnBody", "{\"key\": $(key)}"));
	}
	
	private String getUrl(String key) {
		return String.format(FILE_URL_FORMAT, domain, key);
	}
	
	private class MyRet {
	    private String key;
	}
}
