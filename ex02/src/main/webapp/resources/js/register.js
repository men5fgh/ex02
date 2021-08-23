/**
 * 
 */
function checkExtension(fileName,fileSize){
	// 정규식(파일의 확장자가 .exe, .sh, .zip, .alz등은 파일 첨부할 수 없게 정규식)
	var regex=new RegExp("(.*?)\.(exe|sh|zip|alz)$");
	var maxsize=5242880;	// 5MB
	if(fileSize>=maxsize){
		alert("파일 사이즈 초과");
		return false;
	}
	if(regex.test(fileName)){
		alert("해당 종류의 파일은 업로드 할 수 없습니다.");
		return false;
	}
	return true;
}// checkExtension end...
function showUploadResult(uploadResultArr){
	if(!uploadResultArr || uploadResultArr.length==0){return;}
	
	var uploadUL = $(".uploadResult ul");
	var str="";
	
	$(uploadResultArr).each(function(i,obj){
		// image형태
		if(obj.filetype){
			var fileCallPath=encodeURIComponent(obj.uploadpath+"/s_"+obj.uuid+"_"+obj.filename);
			str+="<li data-path='"+ obj.uploadpath +"'";
			str+=" data-uuid='" + obj.uuid + "' data-filename='" + obj.filename + "' data-type='" + obj.filetype + "'" + "><div>";
			str+="<span>"+obj.filename+"</span>";
			str+="<button type='button' class='btn btn-warning btn-circle'><i class='fa fa-times'></i>"+obj.filename+"</button>";
			str+="<img src='/display?filename="+fileCallPath+"'>";
			str+="</div></li>";
		}else{// 그렇지 않은 형태
			var fileCallPath=encodeURIComponent(obj.uploadpath+"/"+obj.uuid+"_"+obj.filename);
			var fileLink=fileCallPath.replace(new RegExp(/\\/g),"/");
			
			str+="<li><div>";
			str+="<span>"+obj.filename+"</span>";
			// str+="<button type='button' class='btn btn-warning btn-circle'><i class='fa fa-times'></i>"+obj.filename+"</button>";
			str+="<button type='button' class='btn btn-warning btn-circle'><i class='fa fa-times'></i>"+ 닫기 +"</button>";
			str+="</li></div>";
			
		}
	})
	uploadUL.append(str);
}


$(document).ready(function(){
	// form 태그를 선택 - submit을 해야 되니까
	var formObj=$("form[role='form']");
	// 글쓰기 버튼을 클릭하면,
	$("input[type='submit']").on("click",function(e){
		e.preventDefault();
		
		var str = "";
		$(".uploadResult ul li").each(function(i,obj){
			var jobj=$(obj);
			str+="<input type='text' name='attachList["+i+"].filename' value='" + jobj.data("filename") + "'>";
			str+="<input type='text' name='attachList["+i+"].uuid' value='" + jobj.data("uuid") + "'>";
			str+="<input type='text' name='attachList["+i+"].uploadppath' value='" + jobj.data("path") + "'>";
			str+="<input type='text' name='attachList["+i+"].filetype' value='" + jobj.data("type") + "'>";
		})
		// $(".uploadResult ul").html(str);
		formObj.append(str).submit();
		
		
	})// $("input[type='submit']").on("click",function(e){ end...
	$("input[type='file']").change(function(e){
		// <form>와 같은 역할
		var formData = new FormData();
		// <input type="file" name="uploadFile" multiple> 선택
		var inputFile=$("input[name='uploadFile']")
		var files=inputFile[0].files;
		//console.log(files);
		for(var i=0;i<files.length;i++){
			// checkExtension함수 호출(파일 종류, 파일 용량 체크)
			if(!checkExtension(files[i].name,files[i].size)){
				return false;
			}
			formData.append("uploadFile",files[i])
		}
		$.ajax({
			url:"/uploadAjaxAction",
			processData:false,
			contentType:false,
			data:formData,
			type:"post",
			dataType:"json",
			success:function(result){
				//alert("aaaaa");
				console.log(result);
				showUploadResult(result); //showUploadResult함수 호출
			}
		})
	})
})// $(document).ready(function(){ end
