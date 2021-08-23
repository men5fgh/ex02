/**
 * 
 */
$(document).ready(function(){
	$("#uploadBtn").on("click",function(){
		var formData = new FormData();
		var inputFile=$("input[name='uploadFile']")
		var files=inputFile[0].files;
		//console.log(files);
		for(var i=0;i<files.length;i++){
			formData.append("uploadFile",files[i])
		}
		$.ajax({
			url:"/uploadAjaxAction",
			processData:false,
			contentType:false,
			data:formData,
			type:"post",
			success:function(result){
				alert("Uploaded");
			}
		})
		
	})
	
})