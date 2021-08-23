package org.zerock.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.PageDTO;
import org.zerock.service.BoardService;

@Controller
@RequestMapping("board")
public class BoardController {
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	@Autowired
	//@Autowired(BoardService service new BoardService();)
	private BoardService service;
	// 용도 : 글쓰기 화면
	@GetMapping("register")
	public void register() {
		logger.info("register");
	}
	// 용도 : 글쓰기 화면에서 글쓰기 버튼을 클릭
	@PostMapping("register")
	public String registerPost(BoardVO board) {
		logger.info("registerPost" + board);
		// 사용자가 파일선택을 클릭해서 파일업로드를 하나라도 했으면,
		if(board.getAttachList()!=null) {	
			// 그 파일에 대한 정보를 for문을 이용해서 가져와라
			board.getAttachList().forEach(attach->logger.info("attach 값 : "+attach));
		}
		
		service.register(board);
		return "redirect:/board/list";
	}
	@GetMapping("list")
	public void list(Model model,Criteria cri) {
		logger.info("list");
		int count=service.getTotalCount(cri);
		//model.addAttribute("list", service.getList());
		model.addAttribute("list", service.getListWithPaing(cri));
		model.addAttribute("pageMaker", new PageDTO(cri,count));
	}
	@GetMapping("get")
	public void get(@RequestParam("bno") int bno,Model model) {
		model.addAttribute("board",service.get(bno));
	}
	@PostMapping("modify")
	public String modify(BoardVO board, RedirectAttributes rttr) {
		logger.info("modify");
		if(service.modify(board)) {
			rttr.addAttribute("result", "success");
			rttr.addAttribute("bno", board.getBno());
		}
		return "redirect:/board/get";
	}
	@PostMapping("remove")
	public String remove(@RequestParam("bno") int bno) {
		logger.info("remove");
		service.remove(bno);
		return "redirect:/board/list";
		
	}
	@GetMapping(value="getAttachList", produces=MediaType.APPLICATION_JSON_UTF8_VALUE) // select라서 get을 사용함
	@ResponseBody
	public ResponseEntity<ArrayList<BoardAttachVO>> getAttachList(int bno){
		logger.info("getAttachList = " + bno);
		return new ResponseEntity<>(service.getAttachList(bno), HttpStatus.OK);
	}
}
























