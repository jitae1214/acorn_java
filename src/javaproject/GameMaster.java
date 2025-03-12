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
    public static final int GHOST_FORCE_MOVE = -9999; // 유령 강제 이동
   

    public GameMaster() {
        this.board = new ArrayList<>();
        this.userLoc = 0;  // 시작 위치
        this.ghostLoc = 0; // 유령 시작 위치
        this.ghostDistance = 3; // 유령 기본 이동 거리
        this.buff = "normal";  // 기본 이동 효과
        this.random = new Random();
        initializeBoard();
    }
    

    // 보드 칸 목록
    // NormalStage: 일반 칸
    // EventStage: 이벤트 칸
    // ItemStage: 아이템 칸
    // GhostStage: 유령 칸
    // ForceMove: 강제 이동 칸
    // BuffStage: 버프 효과 칸
 
    // 보드 초기화 - 이미지 기반으로 맵 구성
    private void initializeBoard() {
        // 1-7번 칸
        board.add(new NormalStage());      // 1: 시작
        board.add(new NormalStage());      // 2: 일반칸
        board.add(new EventStage());      // 3: 이벤트
        board.add(new GhostStage());      // 4: 유령
        board.add(new EventStage());      // 5: 이벤트
        board.add(new BuffStage("double"));  // 6: 아이템 !이동거리 2배
        board.add(new EventStage());      // 7: 이벤트

        // 8-14번 칸
        board.add(new NormalStage());      // 8: 일반칸
        board.add(new ForceMove(-1));     // 9: 강제이동
        board.add(new GhostStage());      // 10: 유령
        board.add(new EventStage());      // 11: 이벤트
        board.add(new BuffStage("half")); // 12: 아이템 !이동거리 반감
        board.add(new ForceMove(2));      // 13: 강제이동
        board.add(new EventStage());      // 14: 이벤트

        // 15-21번 칸
        board.add(new NormalStage());      // 15: 일반칸
        board.add(new EventStage());      // 16: 이벤트
        board.add(new GhostStage());      // 17: 유령
        board.add(new EventStage());      // 18: 이벤트
        board.add(new GhostStage());      // 19: 유령
        board.add(new ForceMove(-1));     // 20: 강제이동
        board.add(new EventStage());      // 21: 이벤트

        // 22-30번 칸
        board.add(new NormalStage());      // 22: 일반칸
        board.add(new NormalStage());      // 23: 일반칸
        board.add(new ForceMove(GHOST_FORCE_MOVE));      // 24: 강제이동 !유령 앞으로 이동
        board.add(new EventStage());      // 25: 이벤트
        board.add(new GhostStage());      // 26: 유령
        board.add(new BuffStage("gDouble"));       // 27: 아이템 !유령 이동칸 2배
        board.add(new GhostStage());      // 28: 유령
        board.add(new EventStage());      // 29: 이벤트
        board.add(new NormalStage());      // 30: 골
    }

    // 주사위 던지는 메서드
//    public int diceRoll() {
//        return random.nextInt(6) + 1;
//    }
    
    // 디버깅용 주사위 스캐너로 원하는 값 입력
    public int diceRoll() {
    	Scanner scanner = new Scanner(System.in);
    	int dice = scanner.nextInt();
    	return dice;
    }

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
		case "gDouble":
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
                switch(buff) {
				case "double":
					System.out.println("이동거리 2배 효과를 획득했습니다.");
					break;
				case "half":
					System.out.println("이동거리 반감 효과를 획득했습니다.");
					break;
				case "plus1":
					System.out.println("이동거리 +1 효과를 획득했습니다.");
					break;
				case "minus1":
					System.out.println("이동거리 -1 효과를 획득했습니다.");
					break;
				case "gDouble":
					System.out.println("유령 이동거리 2배 효과를 획득했습니다.");
					break;
                }
            }
            // 강제 이동 파트
            else if (currentStage instanceof ForceMove) {
                int forceMove = ((ForceMove) currentStage).getForceStage();
             // 유령 위치 앞으로 이동하는 케이스 파트
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
    
    public int ghostForceLoc() {
    	return getUserLoc() - (getUserLoc() - getGhostLoc()) + 1;
    }

    // 골 도착 확인
    public boolean checkGoal() {
        return userLoc == BOARD_SIZE - 1;
    }

    // 보드 상태 출력
    public void printBoard() {
        // 첫 번째 줄 (1-7)
        System.out.print("   ");
        for (int i = 1; i <= 7; i++) {
            System.out.printf("%-6d", i);  // 4칸으로 통일
        }
        System.out.println();
        System.out.print("   ");
        for (int i = 0; i < 7; i++) {
            printSquare(i);
            if (i < 6) System.out.print(" - ");
        }
        System.out.println();

        // 두 번째 줄 (8-14)
        System.out.print("   ");
        for (int i = 8; i <= 14; i++) {
            System.out.printf("%-6d", i);  // 4칸으로 통일
        }
        System.out.println();
        System.out.print("   ");
        for (int i = 7; i < 14; i++) {
            printSquare(i);
            if (i < 13) System.out.print(" - ");
        }
        System.out.println();

        // 세 번째 줄 (15-21)
        System.out.print("   ");
        for (int i = 15; i <= 21; i++) {
            System.out.printf("%-6d", i);  // 4칸으로 통일
        }
        System.out.println();
        System.out.print("   ");
        for (int i = 14; i < 21; i++) {
            printSquare(i);
            if (i < 20) System.out.print(" - ");
        }
        System.out.println();

        // 네 번째 줄 (22-30)
        System.out.print("   ");
        for (int i = 22; i <= 30; i++) {
            System.out.printf("%-6d", i);  // 4칸으로 통일
        }
        System.out.println();
        System.out.print("   ");
        for (int i = 21; i < 30; i++) {
            printSquare(i);
            if (i < 29) System.out.print(" - ");
        }
        System.out.println();

        System.out.println("\n현재 위치: " + (userLoc + 1) + "번 칸");
        System.out.println("현재 버프: " + buff);
    }



    private void printSquare(int index) {
        String symbol;
        Stage currentStage = board.get(index);

        if (index == userLoc) {
            symbol = "[P]";  // 플레이어 위치
        } else if (index == 0) {
            symbol = "[S]";  // 시작 지점
        } else if (index == BOARD_SIZE - 1) {
            symbol = "[G]";  // 골 지점
        } else if (currentStage instanceof NormalStage) {
            symbol = "[ ]";  // 일반 칸
        } else if (currentStage instanceof GhostStage) {
            symbol = "[G]";  // 유령 칸
        } else if (currentStage instanceof EventStage) {
            symbol = "[E]";  // 이벤트 칸
        } else if (currentStage instanceof BuffStage) {
            symbol = "[I]";  // 아이템 칸
        } else if (currentStage instanceof ForceMove) {
            symbol = "[F]";  // 강제이동 칸
        } else {
            symbol = "[?]";  // 알 수 없는 칸
        }

        System.out.print(symbol);
    }


    // getter/setter 메서드들
    public int getUserLoc() {
        return userLoc;
    }
	   
    
	public int getGhostLoc() {
		return ghostLoc;
	}
	
	
}