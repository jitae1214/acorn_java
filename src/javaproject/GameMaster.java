package javaproject;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class GameMaster {
    private ArrayList<Stage> board;  // 보드 맵
    private int userLoc;            // 유저 현재위치
    private String buff;            // 유저 이동거리 변화 효과
    private static final int BOARD_SIZE = 30;  // 보드 크기
    private Random random;          // 주사위 용 랜덤 객체
    private int ghostLoc;           // 유령 위치
    private int ghostDistance;     // 유령 이동 거리
    private String currentMapStyle; // 현재 맵 스타일
    private MapManager mapManager;  // 맵 관리자
    static final int GHOST_FORCE_MOVE = -9999;

    public GameMaster() {
        this.userLoc = 0;  // 시작 위치
        this.ghostLoc = 0; // 유령 시작 위치
        this.ghostDistance = 3; // 유령 기본 이동 거리
        this.buff = "normal";  // 버프 초기값 설정
        this.random = new Random();
        this.mapManager = new MapManager();
        this.board = mapManager.selectAndLoadMap();
        this.currentMapStyle = mapManager.getCurrentMapStyle();
    }

    // 주사위 던지는 메서드
    public int diceRoll() {
    	buff = "normal"; // 주사위 굴릴 때마다 버프 초기화
        return random.nextInt(6) + 1;
    }
    
//    public int diceRoll() {
//    	Scanner sc = new Scanner(System.in);
//    	int dice = sc.nextInt();
//    	return dice;
//    }

    // 유저 움직이는 메서드
    public int userMove(int distance) {
        // 버프 효과 적용
        int actualDistance = applyBuff(distance);

        // 새로운 위치 계산
        int newLocation = userLoc + actualDistance;

        // 보드 크기를 넘어가지 않도록 처리
        if (newLocation >= BOARD_SIZE) {
            userLoc = BOARD_SIZE - 1;
        } else if (newLocation < 0) {
            userLoc = 0;
        } else {
            userLoc = newLocation;
        }

        // 현재 위치의 스테이지 효과 적용
        nowLocation();

        return userLoc;
    }

    // 유령 이동 메서드
    public int ghostMove(int ghostDistance) {
    	int actualGhostDistance = applyGhostBuff(ghostDistance);
    	
    	ghostLoc = ghostLoc + actualGhostDistance;
    	return actualGhostDistance;	    	
    }

    // 버프 효과를 적용하는 private 메서드
    private int applyBuff(int distance) {
        // buff가 null이면 기본값 "normal"로 설정
        if (buff == null) {
            buff = "normal";
            return distance;
        }
        
        switch(buff.toLowerCase()) {
            case "double":
                return distance * 2;
            case "half":
                return distance / 2;
            case "plus1":
                return distance + 1;
            case "minus1":
                return distance - 1;
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
    public void nowLocation() {
        if (userLoc >= 0 && userLoc < board.size()) {
            Stage currentStage = board.get(userLoc);
            currentStage.도착(userLoc);

            // 각 스테이지 타입별 특수 효과 처리
            if (currentStage instanceof BuffStage) {
                buff = ((BuffStage) currentStage).getBuff();
                System.out.println("버프 효과 획득: " + buff);
            }
            else if (currentStage instanceof ForceMove) {
                int forceMove = ((ForceMove) currentStage).getForceStage();
                if (forceMove == GHOST_FORCE_MOVE) {
                    forceMove = -(userLoc - ghostLoc) + 1;
                    System.out.println("유령 앞으로 " + forceMove + "칸 강제 이동!");
                } else {
                    System.out.println(forceMove + "칸 강제 이동!");
                }
                userMove(forceMove);
                
                
            }
            else if(currentStage instanceof GhostStage) {
                ghostMove(ghostDistance);
                System.out.println("유령이 이동했습니다.");
                System.out.println("유령 위치: " + (ghostLoc + 1) + "번 칸");
                if (ghostLoc == getUserLoc()) {
                    System.out.println("유령에게 잡혔습니다.");
                    System.out.println("게임이 종료되었습니다.");
                    System.exit(0);
                }
            }
            else if(currentStage instanceof EventStage) {
                
            }
            else if(currentStage instanceof NormalStage) {
                System.out.println("일반 칸에 도착하였습니다 어떠한 일도 일어나지 않았습니다");
            }
        }
    }
    
    

    // 골 도착 확인
    public boolean checkGoal() {
        return userLoc == BOARD_SIZE - 1;
    }

    // 보드 상태 출력
    public void printBoard() {
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
        switch(mapManager.getBoardVisualStyle()) {
            case "BLOCK_PUZZLE":
                System.out.println(mapManager.getBlockPuzzleBoard());
                break;
            default:
                // RPG 스타일 보드 출력
                printRPGStyle();
        }

        // 범례 출력
        System.out.println("\n" + BOLD + "[범례]" + RESET);
        System.out.println(YELLOW + "[P]" + RESET + " : 플레이어   " +
                         RED + "[G]" + RESET + " : 유령/유령칸   " +
                         BLUE + "[E]" + RESET + " : 이벤트칸");
        System.out.println(CYAN + "[I]" + RESET + " : 아이템칸    " +
                         PURPLE + "[F]" + RESET + " : 강제이동칸  " +
                         "[ ]" + " : 일반칸");

        // 진행 상태바 출력
        int progress = (userLoc * 100) / (BOARD_SIZE - 1);
        System.out.println("\n진행률: " + progress + "%");
        System.out.print("[");
        for (int i = 0; i < 50; i++) {
            if (i < (progress/2)) {
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
        printRPGTopBorder(0, 7);
        printRPGNumberRow(1, 7);
        printRPGSquareRow(0, 7);
        printRPGBottomBorder(0, 7, false);

        // 두 번째 줄 (8-14)
        printRPGTopBorder(7, 14);
        printRPGNumberRow(8, 14);
        printRPGSquareRow(7, 14);
        printRPGBottomBorder(7, 14, false);

        // 세 번째 줄 (15-21)
        printRPGTopBorder(14, 21);
        printRPGNumberRow(15, 21);
        printRPGSquareRow(14, 21);
        printRPGBottomBorder(14, 21, false);

        // 네 번째 줄 (22-30)
        printRPGTopBorder(21, 30);
        printRPGNumberRow(22, 30);
        printRPGSquareRow(21, 30);
        printRPGBottomBorder(21, 30, true);
    }

    // RPG 스타일 상단 테두리 출력
    private void printRPGTopBorder(int start, int end) {
        System.out.print("╔");
        for (int i = start; i < end; i++) {
            System.out.print("═══════");
            if (i < end - 1) System.out.print("╦");
        }
        System.out.println("╗");
    }

    // RPG 스타일 숫자 줄 출력
    private void printRPGNumberRow(int start, int end) {
        System.out.print("║");
        for (int i = start; i <= end; i++) {
            if (i < 10) {
                System.out.printf("   %d   ", i);
            } else {
                System.out.printf("  %d   ", i);
            }
            if (i < end) System.out.print("║");
        }
        System.out.println("║");
    }

    // RPG 스타일 칸 줄 출력
    private void printRPGSquareRow(int start, int end) {
        System.out.print("║");
        for (int i = start; i < end; i++) {
            System.out.print("  ");
            printColorSquare(i);
            System.out.print("  ");
            if (i < end - 1) System.out.print("║");
        }
        System.out.println("║");
    }

    // RPG 스타일 하단 테두리 출력
    private void printRPGBottomBorder(int start, int end, boolean isLast) {
        System.out.print(isLast ? "╚" : "╠");
        for (int i = start; i < end; i++) {
            System.out.print("═══════");
            if (i < end - 1) System.out.print(isLast ? "╩" : "╬");
        }
        System.out.println(isLast ? "╝" : "╣");
    }

    private void printColorSquare(int index) {
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
            System.out.print(BOLD + RED + "[X]" + RESET);  // 유저와 유령이 같은 위치
        } else if (index == userLoc) {
            System.out.print(BOLD + YELLOW + "[P]" + RESET);  // 플레이어 위치
        } else if (index == ghostLoc) {
            System.out.print(BOLD + RED + "[G]" + RESET);  // 유령 위치
        } else if (index == 0) {
            System.out.print(BOLD + GREEN + "[S]" + RESET);  // 시작 지점
        } else if (index == BOARD_SIZE - 1) {
            System.out.print(BOLD + PURPLE + "[F]" + RESET);  // 골 지점
        } else if (currentStage instanceof NormalStage) {
            System.out.print("[ ]");  // 일반 칸
        } else if (currentStage instanceof GhostStage) {
            System.out.print(BOLD + RED + "[G]" + RESET);  // 유령 칸
        } else if (currentStage instanceof EventStage) {
            System.out.print(BOLD + BLUE + "[E]" + RESET);  // 이벤트 칸
        } else if (currentStage instanceof BuffStage) {
            System.out.print(BOLD + CYAN + "[I]" + RESET);  // 아이템 칸
        } else if (currentStage instanceof ForceMove) {
            System.out.print(BOLD + PURPLE + "[F]" + RESET);  // 강제이동 칸
        } else {
            System.out.print("[?]");  // 알 수 없는 칸
        }
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
             printBoard();
         }

         System.out.println("\n=== 게임 클리어! ===");
         scanner.close();
    }

    // getter/setter 메서드들
    public int getUserLoc() {
        return userLoc;
    }

    public void setUserLoc(int userLoc) {
        this.userLoc = userLoc;
    }

    public String getBuff() {
        return buff;
    }

    public void setBuff(String buff) {
        this.buff = buff;
    }
    
    public int getGhostLoc() {
        return ghostLoc;
    }

    // 보드 getter 메서드 추가
    public ArrayList<Stage> getBoard() {
        return board;
    }
}