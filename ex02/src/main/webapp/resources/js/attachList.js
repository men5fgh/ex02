/**
 * 
 */
$(document).ready(function(){
	var bno=$("#bno").val();
	alert(bno);
	$.getJSON("getAttachList", {bno:bno}, function(arr){
		// console.log(arr);
		var str="";
		$(arr).each(function(i, attach){
			if(attach.filetype) {
				var fileCallPath=encodeURIComponent(attach.uploadpath + "/s_" + attach.uuid + "_");
				str+="<li data-path='" + attach.uploadpath + "' data-uuid='" + attach.uuid + "' data-filename = '" + attach.filename + "' data-type='" + attach.filetype + "'><div>";
				str+="<img src='/display?filename=" + fileCallPath + "'>";
				str+="</div></li>";
			} // if end
			else{
				str+="<li data-path='" + attach.uploadpath + "' data-uuid='" + attach.uuid + "' data-filename = '" + attach.filename + "' data-type='" + attach.filetype + "'><div>";
				str+="<span>" + attach.filename + "</span><br>";
				str+="<img src='/display?filename=" + fileCallPath + "'>";
				str+="</div></li>";
			} // else end
		}) // each end
		$(".uploadResult ul").html(str);
	})
})