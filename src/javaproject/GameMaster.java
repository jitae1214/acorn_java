package javaproject;

import java.util.ArrayList;
import java.util.Scanner;

/*
주요 기능:
- 게임의 핵심 로직을 관리
- 플레이어와 유령의 위치, 이동 처리
- 버프 효과 및 특수 칸 효과 처리
- 게임 상태 관리

개선 가능한 부분:
- 게임 저장/로드 기능 추가 필요
- 난이도 조절 시스템 구현 필요
- 멀티플레이어 지원 고려
*/
class GameMaster {
	private MapManager mapManager; // 맵 관리자
	private QuizManager quizManager;

	private final int BOARD_SIZE = 30; // 보드 크기
	private final int GHOST_FORCE_MOVE = -9999;

	private ArrayList<Stage> board; // 보드 맵
	private int previousLoc; // 이동 전 위치를 저장하기 위한 변수

	private int userLoc; // 유저 현재위치
	private String buff; // 유저 이동거리 변화 효과

	private int ghostLoc; // 유령 위치
	private int ghostDistance; // 유령 이동 거리
	private String currentMapStyle; // 현재 맵 스타일

	public GameMaster() {
		this.userLoc = 0; // 시작 위치
		this.buff = "normal"; // 버프 초기값 설정
		this.ghostLoc = 0; // 유령 시작 위치
		this.ghostDistance = 3; // 유령 기본 이동 거리
		this.previousLoc = 0; // previousLoc 초기화 추가
		this.mapManager = new MapManager();
		this.board = mapManager.selectAndLoadMap();
		this.currentMapStyle = mapManager.getCurrentMapStyle();
		this.quizManager = new QuizManager();
	}

	// 주사위 던지는 메서드
	public int diceRoll() {
		int dice = (int) (Math.random() * 6 + 1);
		int buffedDice = applyBuff(dice);

		// "gDouble" 버프는 초기화하지 않음
		if (buff == null || !buff.equals("gDouble")) {
			buff = "normal"; // 플레이어 버프만 초기화
		}

		return buffedDice;
	}

	// 유저 움직이는 메서드
	private int userMove(int distance) {
		// 현재 위치를 previousLoc에 저장
		previousLoc = userLoc;

		// 버프 효과 적용한 새로운 위치 계산
		int newLocation = userLoc + applyBuff(distance);

		// 보드 크기를 넘어가지 않도록 처리
		if (newLocation >= BOARD_SIZE) {
			userLoc = BOARD_SIZE - 1;
		} else if (newLocation < 0) {
			userLoc = 0;
		} else {
			userLoc = newLocation;
		}

		return userLoc;
	}

	// 유령 이동 메서드
	public int ghostMove(int ghostDistance) {
		int actualGhostDistance = applyGhostBuff(ghostDistance);
		ghostLoc = ghostLoc + actualGhostDistance;

		// 유령 버프 사용 후 초기화
		if (buff != null && buff.equals("gDouble")) {
			buff = "normal";
		}

		return actualGhostDistance;
	}

	// 버프 효과를 적용하는 private 메서드
	private int applyBuff(int distance) {
		// buff가 null이면 기본값 "normal"로 설정
		if (buff == null) {
			buff = "normal";
			return distance;
		}

		switch (buff.toLowerCase()) {
		case "double":
			return distance * 2;
		case "half":
			return distance / 2;
		case "plus1":
			return distance + 1;
		case "minus1":
			return distance - 1;
		case "gdouble":
			this.ghostDistance *= 2;
			return distance;
		default:
			return distance;
		}
	}

	private int applyGhostBuff(int ghostDistance) {
		switch (buff.toLowerCase()) {
		case "gdouble":
			return ghostDistance * 2;
		default:
			return ghostDistance;
		}
	}

	// 현재 위치의 칸 정보 확인
	private void nowLocation() {
		if (userLoc >= 0 && userLoc < board.size()) {
			Stage currentStage = board.get(userLoc);
			currentStage.도착(userLoc);

			// 각 스테이지 타입별 특수 효과 처리
			if (currentStage instanceof BuffStage) {
				buff = ((BuffStage) currentStage).getBuff();
				// 버프 효과 메시지는 BuffStage 클래스에서 이미 출력하므로 여기서는 제거
			} else if (currentStage instanceof ForceMove) {
				int forceMove = ((ForceMove) currentStage).getForceStage();
				if (forceMove == GHOST_FORCE_MOVE) {
					forceMove = -(userLoc - ghostLoc) + 1;
					System.out.println("유령 앞으로 " + forceMove + "칸 강제 이동!");
				} else {
					System.out.println(forceMove + "칸 강제 이동!");
				}
				userMove(forceMove);

			} else if (currentStage instanceof GhostStage) {
				ghostMove(ghostDistance);
			} else if (currentStage instanceof EventStage) {
				boolean eventQuiz = quizManager.answerCheck(userLoc);
				if (eventQuiz) {
					System.out.println("\n정답입니다!!!\n");
				} else {
					System.out.println("\n오답입니다...\n");
					userLoc = previousLoc; // 오답 시 이전 위치로 돌아감
					ghostMove(ghostDistance);
					System.out.println("유령이 이동했습니다.");
					System.out.println("유령 위치: " + (ghostLoc + 1) + "번 칸");
					if (ghostLoc >= userLoc) { // 수정된 부분: >= 대신 == 사용
						System.out.println("유령에게 잡혔습니다.");
						System.out.println("게임이 종료되었습니다.");
						System.exit(0);
					}
				}
			} else if (currentStage instanceof NormalStage) {
				System.out.println("일반 칸에 도착하였습니다 어떠한 일도 일어나지 않았습니다");
			}
		}
	}

	// 골 도착 확인
	private boolean checkGoal() {
		return userLoc == BOARD_SIZE - 1;
	}

	// 보드 상태 출력
	private void printBoard() {
		String RESET = "\u001B[0m";
		String RED = "\u001B[31m";
		String GREEN = "\u001B[32m";
		String YELLOW = "\u001B[33m";
		String BLUE = "\u001B[34m";
		String PURPLE = "\u001B[35m";
		String CYAN = "\u001B[36m";
		String BOLD = "\u001B[1m";

		// 상단 정보 출력
		System.out.println("\n" + BOLD + "=== 유령 피하기 게임 ===" + RESET);
		System.out.println("현재 맵: " + GREEN + mapManager.getCurrentMapStyle() + RESET);
		System.out.println("현재 위치: " + YELLOW + (userLoc + 1) + "번 칸" + RESET);
		System.out.println("현재 버프: " + CYAN + buff + RESET + "\n");

		// 맵 스타일에 따라 다른 출력 방식 사용
		switch (mapManager.getBoardVisualStyle()) {
		case "BLOCK_PUZZLE":
			System.out.println(mapManager.getBlockPuzzleBoard());
			break;
		default:
			// RPG 스타일 보드 출력
			printRPGStyle();
		}

		// 범례 출력
		System.out.println("\n" + BOLD + "[범례]" + RESET);
		System.out.println(YELLOW + "[P]" + RESET + " : 플레이어   " + RED + "[G]" + RESET + " : 유령/유령칸   " + BLUE + "[E]"
				+ RESET + " : 이벤트칸");
		System.out.println(
				CYAN + "[I]" + RESET + " : 아이템칸    " + PURPLE + "[F]" + RESET + " : 강제이동칸  " + "[ ]" + " : 일반칸");

		// 진행 상태바 출력
		int progress = (userLoc * 100) / (BOARD_SIZE - 1);
		System.out.println("\n진행률: " + progress + "%");
		System.out.print("[");
		for (int i = 0; i < 50; i++) {
			if (i < (progress / 2)) {
				System.out.print("█");
			} else {
				System.out.print("░");
			}
		}
		System.out.println("]");
	}

	// RPG 스타일 출력을 위한 private 메서드
	private void printRPGStyle() {
		// 첫 번째 줄 (1-7)
		GameMapPrint.printRPGTopBorder(0, 7);
		GameMapPrint.printRPGNumberRow(1, 7);
		GameMapPrint.printRPGSquareRow(0, 7, board, userLoc, ghostLoc, BOARD_SIZE);
		GameMapPrint.printRPGBottomBorder(0, 7, false);

		// 두 번째 줄 (8-14)
		GameMapPrint.printRPGTopBorder(7, 14);
		GameMapPrint.printRPGNumberRow(8, 14);
		GameMapPrint.printRPGSquareRow(7, 14, board, userLoc, ghostLoc, BOARD_SIZE);
		GameMapPrint.printRPGBottomBorder(7, 14, false);

		// 세 번째 줄 (15-21)
		GameMapPrint.printRPGTopBorder(14, 21);
		GameMapPrint.printRPGNumberRow(15, 21);
		GameMapPrint.printRPGSquareRow(14, 21, board, userLoc, ghostLoc, BOARD_SIZE);
		GameMapPrint.printRPGBottomBorder(14, 21, false);

		// 네 번째 줄 (22-30)
		GameMapPrint.printRPGTopBorder(21, 30);
		GameMapPrint.printRPGNumberRow(22, 30);
		GameMapPrint.printRPGSquareRow(21, 30, board, userLoc, ghostLoc, BOARD_SIZE);
		GameMapPrint.printRPGBottomBorder(21, 30, true);
	}

	public void run() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("=== 보드게임 테스트 시작 ===");
		printBoard();

		while (!checkGoal()) {
			System.out.println("\n엔터를 누르면 주사위를 굴립니다...");
			scanner.nextLine();

			int dice = diceRoll();

			System.out.println("주사위: " + dice);

			userMove(dice);
			// 현재 위치의 스테이지 효과 적용
			nowLocation();
			printBoard();
		}

		System.out.println("\n=== 게임 클리어! ===");
		scanner.close();
	}
}