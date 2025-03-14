package javaproject;

import java.util.ArrayList;

public class GameMapPrint {
	// RPG 스타일 상단 테두리 출력
	static void printRPGTopBorder(int start, int end) {
		System.out.print("╔");
		for (int i = start; i < end; i++) {
			System.out.print("═══════");
			if (i < end - 1)
				System.out.print("╦");
		}
		System.out.println("╗");
	}

	// RPG 스타일 숫자 줄 출력
	static void printRPGNumberRow(int start, int end) {
		System.out.print("║");
		for (int i = start; i <= end; i++) {
			if (i < 10) {
				System.out.printf("   %d   ", i);
			} else {
				System.out.printf("  %d   ", i);
			}
			if (i < end)
				System.out.print("║");
		}
		System.out.println("║");
	}

	// RPG 스타일 칸 줄 출력
	static void printRPGSquareRow(int start, int end, ArrayList<Stage> board, int userLoc, int ghostLoc,
			int BOARD_SIZE) {
		System.out.print("║");
		for (int i = start; i < end; i++) {
			System.out.print("  ");
			GameMapPrint.printColorSquare(board, i, userLoc, ghostLoc, BOARD_SIZE);
			System.out.print("  ");
			if (i < end - 1)
				System.out.print("║");
		}
		System.out.println("║");
	}

	// RPG 스타일 하단 테두리 출력
	static void printRPGBottomBorder(int start, int end, boolean isLast) {
		System.out.print(isLast ? "╚" : "╠");
		for (int i = start; i < end; i++) {
			System.out.print("═══════");
			if (i < end - 1)
				System.out.print(isLast ? "╩" : "╬");
		}
		System.out.println(isLast ? "╝" : "╣");
	}

	static void printColorSquare(ArrayList<Stage> board, int index, int userLoc, int ghostLoc, int BOARD_SIZE) {
		String RESET = "\u001B[0m";
		String RED = "\u001B[31m";
		String GREEN = "\u001B[32m";
		String YELLOW = "\u001B[33m";
		String BLUE = "\u001B[34m";
		String PURPLE = "\u001B[35m";
		String CYAN = "\u001B[36m";
		String BOLD = "\u001B[1m";

		Stage currentStage = board.get(index);

		if (index == userLoc && index == ghostLoc) {
			System.out.print(BOLD + RED + "[X]" + RESET); // 유저와 유령이 같은 위치
		} else if (index == userLoc) {
			System.out.print(BOLD + YELLOW + "[P]" + RESET); // 플레이어 위치
		} else if (index == ghostLoc) {
			System.out.print(BOLD + RED + "[G]" + RESET); // 유령 위치
		} else if (index == 0) {
			System.out.print(BOLD + GREEN + "[S]" + RESET); // 시작 지점
		} else if (index == BOARD_SIZE - 1) {
			System.out.print(BOLD + PURPLE + "[F]" + RESET); // 골 지점
		} else if (currentStage instanceof NormalStage) {
			System.out.print("[ ]"); // 일반 칸
		} else if (currentStage instanceof GhostStage) {
			System.out.print(BOLD + RED + "[G]" + RESET); // 유령 칸
		} else if (currentStage instanceof EventStage) {
			System.out.print(BOLD + BLUE + "[E]" + RESET); // 이벤트 칸
		} else if (currentStage instanceof BuffStage) {
			System.out.print(BOLD + CYAN + "[I]" + RESET); // 아이템 칸
		} else if (currentStage instanceof ForceMove) {
			System.out.print(BOLD + PURPLE + "[F]" + RESET); // 강제이동 칸
		} else {
			System.out.print("[?]"); // 알 수 없는 칸
		}
	}
}
