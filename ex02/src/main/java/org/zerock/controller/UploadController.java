package org.zerock.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.BoardAttachVO;

import net.coobird.thumbnailator.Thumbnailator;

@Controller
public class UploadController {
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	@GetMapping("/display")
	@ResponseBody
	public ResponseEntity<byte[]> getFile(String filename){
		logger.info("fileName : "+filename);
		
		File file = new File("c:\\upload\\"+filename);
		
		logger.info("file : "+file);
		
		ResponseEntity<byte[]>result = null;
		
		try {
			HttpHeaders header = new HttpHeaders();
			header.add("Content-Type", Files.probeContentType(file.toPath()));
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file),header,HttpStatus.OK);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	
	
	
	
	@GetMapping(value="uploadForm")
	public void uploadForm() {
		logger.info("uploadForm");
	}
	@PostMapping(value="uploadFormAction")
	public void uploadFormAction(MultipartFile[] uploadFile) {
		String uploadFolder="c:\\upload";
		// for(int i=0;i<uploadFile.length;i++)
		for(MultipartFile multipartFile : uploadFile) {
			logger.info("Upload File Name : "+multipartFile.getOriginalFilename());
			logger.info("Upload File Size : "+multipartFile.getSize());
			
			File saveFile = new File(uploadFolder, multipartFile.getOriginalFilename());
			
			try {
				multipartFile.transferTo(saveFile);
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}
	}
	@GetMapping(value="uploadAjax")
	public void uploadAjax() {
		logger.info("uploadAjax");
	}
	
	// getFolder메서드 선언(page 508)
	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(); //import java.util.Date; import 오늘날짜 : 20210819
		System.out.println("date : "+date);
		// Thu Aug 19 16:08:39 KST 2021 -> 2021-08-19
		String str= sdf.format(date);
		System.out.println("str : "+str);
		// 2021-08-19 -> 2021\08\19
		return str.replace("-", File.separator);
	}
	// checkImageType메서드 선언(page 513)
	private boolean checkImageType(File file) {
		try {
			String contentType=Files.probeContentType(file.toPath());
			// 파일업로드 한 파일이 이미지파일인지 체크
			return contentType.startsWith("image");
		}catch(IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	@PostMapping(value="uploadAjaxAction",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<ArrayList<BoardAttachVO>> uploadAjaxAction(MultipartFile[] uploadFile) {
		logger.info("uploadAjaxAction");
		ArrayList<BoardAttachVO> list=new ArrayList<>();
		// 파일 업로드 할 폴더
		String uploadFolder="c:\\upload";
		
		String uploadFolderPath=getFolder(); // 2021\08\19
		// c:\\upload\\2021\\08\\19
		File uploadPath=new File(uploadFolder,uploadFolderPath);
		// c:\\upload\\2021\\08\\19폴더가 없으면(false)
		if(uploadPath.exists()==false) {
			// c:\\upload\\2021\\08\\19폴더 생성(mkdirs())해라!
			uploadPath.mkdirs();
		}
		
		for(MultipartFile multipartFile : uploadFile) {
			logger.info("Upload File Name : "+multipartFile.getOriginalFilename());
			logger.info("Upload File Size : "+multipartFile.getSize());
			
			BoardAttachVO attachDTO = new BoardAttachVO();
			// Internet Explorer를 제외한 브라우저에서 파일명(uploadFileName=000.png)
			String uploadFileName=multipartFile.getOriginalFilename();
			// Internet Explorer브라우저에서 파일명(uploadFileName=C:\\Users\\GreenArt\\Pictures\\000.png)
			
			uploadFileName=uploadFileName.substring(uploadFileName.lastIndexOf("\\")+1);
			//System.out.println("uploadFileName="+uploadFileName);
			attachDTO.setFilename(uploadFileName);
			
			UUID uuid=UUID.randomUUID();
			System.out.println("uuid.toString()"+uuid.toString());
			// 5e90a722-5811-4e2e-80a3-d8a5459bd9e6_000.png
			uploadFileName=uuid.toString()+"_"+uploadFileName;
			
			
			
			try {
				//   c:\\upload\\2021\\08\\19\\5e90a722-5811-4e2e-80a3-d8a5459bd9e6_000.png
				File saveFile = new File(uploadPath, uploadFileName);
				multipartFile.transferTo(saveFile);
				
				attachDTO.setUuid(uuid.toString());
				attachDTO.setUploadpath(uploadFolderPath);
				
				if(checkImageType(saveFile)) {// 업로드한 파일이 이미지 파일이면,
					attachDTO.setFiletype(true);
					// FileOutputStream :무조건 해당 파일을 생성한다. 존재하는 파일일 경우 덮어쓰기함.
					//                                                         c:\\upload\\2021\\08\\19\\s_5e90a722-5811-4e2e-80a3-d8a5459bd9e6_000.png
					FileOutputStream thumbnail = new FileOutputStream(new File(uploadPath,"s_"+uploadFileName));
					Thumbnailator.createThumbnail(multipartFile.getInputStream(),thumbnail,100,100);
					thumbnail.close();
				}
				list.add(attachDTO);
				
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
		}// end for
		return new ResponseEntity<>(list,HttpStatus.OK);
	}
	
}
