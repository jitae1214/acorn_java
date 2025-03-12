package javaproject;

import java.util.ArrayList;
import java.util.Random;

class GameMaster {
    private ArrayList<Stage> board;  // 보드 맵
    private int userLoc;            // 유저 현재위치
    private String buff;            // 유저 이동거리 변화 효과
    // 이거 스테틱으로 해야하는 이유는??
    private static final int BOARD_SIZE = 30;  // 보드 크기
    private Random random;          // 주사위 용 랜덤 객체

    public GameMaster() {
        this.board = new ArrayList<>();
        this.userLoc = 0;  // 시작 위치
        this.buff = "normal";  // 기본 이동 효과
        this.random = new Random();
        initializeBoard();
    }

    // 보드 초기화 - 이미지 기반으로 맵 구성
    private void initializeBoard() {
    	// 불러오는거는 LoadData에서 불러오는걸로 변경 예정
    	// 밑의 내용을 txt파일로 저장해야 할듯
        // 1-7번 칸
        board.add(new NomalStage());      // 1: 시작
        board.add(new NomalStage());      // 2: 일반칸
        board.add(new QuizStage());      // 3: 이벤트
        board.add(new GhostStage());      // 4: 유령
        board.add(new QuizStage());      // 5: 이벤트
        board.add(new BuffStage());       // 6: 아이템
        board.add(new QuizStage());      // 7: 이벤트

        // 8-14번 칸
        board.add(new NomalStage());      // 8: 일반칸
        board.add(new ForceMove(-1));     // 9: 강제이동
        board.add(new GhostStage());      // 10: 유령
        board.add(new QuizStage());      // 11: 이벤트
        board.add(new BuffStage());       // 12: 아이템
        board.add(new ForceMove(2));      // 13: 강제이동
        board.add(new QuizStage());      // 14: 이벤트

        // 15-21번 칸
        board.add(new NomalStage());      // 15: 일반칸
        board.add(new QuizStage());      // 16: 이벤트
        board.add(new GhostStage());      // 17: 유령
        board.add(new QuizStage());      // 18: 이벤트
        board.add(new GhostStage());      // 19: 유령
        board.add(new ForceMove(-1));     // 20: 강제이동
        board.add(new QuizStage());      // 21: 이벤트

        // 22-30번 칸
        board.add(new NomalStage());      // 22: 일반칸
        board.add(new NomalStage());      // 23: 일반칸
        board.add(new ForceMove(1));      // 24: 강제이동
        board.add(new QuizStage());      // 25: 이벤트
        board.add(new GhostStage());      // 26: 유령
        board.add(new BuffStage());       // 27: 아이템
        board.add(new GhostStage());      // 28: 유령
        board.add(new QuizStage());      // 29: 이벤트
        board.add(new NomalStage());      // 30: 골
    }

    // 주사위 던지는 메서드
    public int diceRoll() {
        return random.nextInt(6) + 1;
    }

    // 유저 움직이는 메서드
    public int userMove(int distance) {
        // 버프 효과 적용
        int actualDistance = applyBuff(distance);

        // 새로운 위치 계산
        int newLocation = userLoc + actualDistance;

        // 보드크기는 30 즉 29를 넘기면 안됨
        // 보드 크기 넘어가지 않게 하는 유효성 검사하는 메서드로 따로 빼두어도 될듯
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
                userMove(forceMove);
                System.out.println(forceMove + "칸 강제 이동!");
            }
        }
    }

    // 골 도착 확인
    public boolean checkGoal() {
        return userLoc == BOARD_SIZE - 1;
    }

    // 보드 상태 출력
    public void printBoard() {
        // 첫 번째 줄 (1-7)
        printNumbers(1, 7);
        printRow(0, 7, false);
        System.out.println();

        // 두 번째 줄 (8-14)
        printNumbers(8, 14);
        printRow(7, 14, true);
        System.out.println();

        // 세 번째 줄 (15-21)
        printNumbers(15, 21);
        printRow(14, 21, false);
        System.out.println();

        // 네 번째 줄 (22-30)
        printNumbers(22, 30);
        printRow(21, 30, true);
        System.out.println();

        System.out.println("\n현재 위치: " + (userLoc + 1) + "번 칸");
        System.out.println("현재 버프: " + buff);
    }

    private void printNumbers(int start, int end) {
        System.out.print("\t");
        for (int i = start; i <= end; i++) {
            System.out.printf("%3d\t ", i);
        }
        System.out.println();
    }

    private void printRow(int start, int end, boolean reverse) {
        System.out.print("   ");
        if (reverse) {
            for (int i = end - 1; i >= start; i--) {
                printSquare(i);
                if (i > start) System.out.print(" - ");
            }
        } else {
            for (int i = start; i < end; i++) {
                printSquare(i);
                if (i < end - 1) System.out.print(" - ");
            }
        }
    }

    private void printSquare(int index) {
        String symbol = "";
        Stage currentStage = board.get(index);

        if (index == userLoc) {
            symbol = "[P]";
        } else if (currentStage instanceof GhostStage) {
            symbol = "[G]";
        } else if (currentStage instanceof QuizStage) {
            symbol = "[E]";
        } else if (currentStage instanceof BuffStage) {
            symbol = "[I]";
        } else if (currentStage instanceof ForceMove) {
            symbol = "[F]";
        } else {
            symbol = "[ ]";
        }

        System.out.print(symbol);
    }

    // 안쓸거같음
    // getter/setter 메서드들
    public int getUserLoc() {
        return userLoc;
    }

    public String getBuff() {
        return buff;
    }

    public void setBuff(String buff) {
        this.buff = buff;
    }
}