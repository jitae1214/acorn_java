package javaproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

//맵 파일 구조:
//각 맵은 텍스트 파일로 저장
//맵 요소는 ,구분자로 구분했음

/*
주요 기능:
- 게임 맵 생성 및 관리
- 맵 파일 로드/저장
- 맵 스타일별 시각화 처리
- 보드 상태 관리

개선 가능한 부분:
- 맵 에디터 기능 추가 고려
- 맵 검증 시스템 강화 필요
- 다양한 맵 형식 지원 확장
*/
public class MapManager {
    Scanner scanner = new Scanner(System.in);
    private ArrayList<Stage> board;
    private String currentMapStyle;
    private final int BOARD_SIZE = 30;
    private final int GHOST_FORCE_MOVE = -9999; // 유령 방향 이동을 위한 기본 값


    public MapManager() {
        this.board = new ArrayList<>();
        this.currentMapStyle = "기본";
    }

    public ArrayList<Stage> selectAndLoadMap() {
        System.out.println("\n=== 맵 선택 ===");
        System.out.println("1. 기본 맵");
        System.out.println("2. 원형 트랙");
        System.out.println("3. 블록 퍼즐");
        System.out.print("맵을 선택하세요 (1-3): ");
        
        int choice = scanner.nextInt();
        
        switch(choice) {
            case 1:
                initializeDefaultBoard();
                currentMapStyle = "기본";
                break;
            case 2:
                loadMapFromFile("circular_track.txt");
                currentMapStyle = "원형 트랙";
                break;
            case 3:
                loadMapFromFile("block_puzzle.txt");
                currentMapStyle = "블록 퍼즐";
                break;
            default:
                System.out.println("잘못된 선택입니다. 기본 맵을 로드합니다.");
                initializeDefaultBoard();
                currentMapStyle = "기본";
        }
        return board;
    }
    public void loadMapFromFile(String filename) {
        board.clear();
        try {
            // maps 폴더의 실제 경로 사용
            String mapPath = "maps/" + filename;
            System.out.println("맵 파일 경로: " + mapPath);
            
            File file = new File(mapPath);
            if (!file.exists()) {
                System.out.println("맵 파일이 존재하지 않습니다: " + mapPath);
                throw new IOException("파일을 찾을 수 없습니다.");
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int totalElements = 0;
            
            while ((line = reader.readLine()) != null && totalElements < BOARD_SIZE) {
                String[] elements = line.trim().split(",");
                for (String element : elements) {
                    if (totalElements < BOARD_SIZE) {
                        addStageToBoard(element.trim());
                        totalElements++;
                    }
                }
            }
            reader.close();

            // 맵이 30칸보다 작으면 나머지는 일반 칸으로 채움
            while (totalElements < BOARD_SIZE) {
                board.add(new NormalStage());
                totalElements++;
            }

            if (board.size() != BOARD_SIZE) {
                System.out.println("맵 크기가 맞지 않습니다. 현재 크기: " + board.size());
                throw new IOException("맵 크기 불일치");
            }
        } catch (Exception e) {
            System.out.println("맵 파일을 불러오는데 실패했습니다. 기본 맵을 로드합니다.");
            System.out.println("에러 메시지: " + e.getMessage());
            System.out.println("에러 발생 위치: " + e.getStackTrace()[0]);
            initializeDefaultBoard();
        }
    }

    private void addStageToBoard(String element) {
        switch(element) {
            case "N": //  N: 일반 칸
                board.add(new NormalStage());
                break;
            case "E": //  E: 이벤트 칸
                board.add(new EventStage());
                break;
            case "G": //  G: 유령 칸
                board.add(new GhostStage());
                break;
            case "B": //  B: 버프 칸
                board.add(new BuffStage());
                break;
            case "F+": //  F+: 앞으로 이동
                board.add(new ForceMove(2));
                break;
            case "F-": //  F-: 뒤로 이동
                board.add(new ForceMove(-1));
                break;
            case "FG": //  FG: 유령 방향 이동
                board.add(new ForceMove(-1)); // ghostForceLoc은 나중에 계산
                break;
            default:
                board.add(new NormalStage());
        }
    }

    public void initializeDefaultBoard() {
        board.clear();
        // 1-7번 칸
        board.add(new NormalStage());     // 1: 시작
        board.add(new NormalStage());     // 2: 일반칸
        board.add(new EventStage());      // 3: 이벤트
        board.add(new GhostStage());      // 4: 유령
        board.add(new EventStage());      // 5: 이벤트
        board.add(new BuffStage("double"));       // 6: 아이템
        board.add(new EventStage());      // 7: 이벤트

        // 8-14번 칸
        board.add(new NormalStage());      // 8: 일반칸
        board.add(new ForceMove(-1));      // 9: 강제이동
        board.add(new GhostStage());      // 10: 유령
        board.add(new EventStage());      // 11: 이벤트
        board.add(new BuffStage("half"));       // 12: 아이템
        board.add(new ForceMove(2));      // 13: 강제이동
        board.add(new EventStage());      // 14: 이벤트

        // 15-21번 칸
        board.add(new NormalStage());      // 15: 일반칸
        board.add(new EventStage());      // 16: 이벤트
        board.add(new GhostStage());      // 17: 유령
        board.add(new EventStage());      // 18: 이벤트
        board.add(new GhostStage());      // 19: 유령
        board.add(new ForceMove(-1));   // 20: 강제이동
        board.add(new EventStage());      // 21: 이벤트

        // 22-30번 칸
        board.add(new NormalStage());      // 22: 일반칸
        board.add(new NormalStage());      // 23: 일반칸
        board.add(new ForceMove(GHOST_FORCE_MOVE));   // 24: 강제이동
        board.add(new EventStage());      // 25: 이벤트
        board.add(new GhostStage());      // 26: 유령
        board.add(new BuffStage("gDouble"));       // 27: 아이템
        board.add(new GhostStage());      // 28: 유령
        board.add(new EventStage());      // 29: 이벤트
        board.add(new NormalStage());      // 30: 골
    }

    public String getCurrentMapStyle() {
        return currentMapStyle;
    }

    // 맵 스타일에 따른 시각적 표현을 위한 메서드 추가
    public String getBoardVisualStyle() {
        switch(currentMapStyle) {
            case "원형 트랙":
                return "CIRCULAR";
            case "블록 퍼즐":
                return "BLOCK_PUZZLE";
            default:
                return "DEFAULT";
        }
    }

    // 원형 트랙 스타일의 좌표 계산을 위한 메서드
    public int[] getCircularPosition(int index) {
        double angle = (2 * Math.PI * index) / BOARD_SIZE;
        int centerX = 15;  // 중심점 X
        int centerY = 10;  // 중심점 Y
        int radius = 8;    // 반지름
        
        int x = centerX + (int)(radius * Math.cos(angle));
        int y = centerY + (int)(radius * Math.sin(angle));
        
        return new int[]{x, y};
    }

    // 블록 퍼즐 스타일의 좌표 계산을 위한 메서드
    public int[] getBlockPuzzlePosition(int index) {
        int row = index / 6;  // 6칸씩 한 줄
        int col = index % 6;
        return new int[]{col * 4, row * 3};  // 4칸 간격으로 가로, 3칸 간격으로 세로
    }

    // 블록 퍼즐 스타일로 보드를 문자열로 변환
    public String getBlockPuzzleBoard() {
        StringBuilder result = new StringBuilder();
        result.append("\n=== 블록 퍼즐 맵 ===\n\n");
        
        // 6x5 그리드로 표시 (30칸)
        for (int row = 0; row < 5; row++) {
            // 윗줄 테두리
            for (int col = 0; col < 6; col++) {
                result.append("┌───┐ ");
            }
            result.append("\n");
            
            // 중간 내용
            for (int col = 0; col < 6; col++) {
                int index = row * 6 + col;
                Stage currentStage = board.get(index);
                String content = getStageSymbol(currentStage);
                // 번호가 10 이상이면 간격 조절
                if (index + 1 >= 10) {
                    result.append("│").append(index + 1).append("│ ");
                } else {
                    result.append("│ ").append(index + 1).append("│ ");
                }
            }
            result.append("\n");
            
            // 아랫줄 테두리
            for (int col = 0; col < 6; col++) {
                result.append("└───┘ ");
            }
            result.append("\n");
        }
        
        // 범례 추가
        result.append("\n[ 범례 ]\n");
        result.append("N: 일반칸  E: 이벤트  G: 유령  B: 버프\n");
        result.append("F+: 앞으로 이동  F-: 뒤로 이동  FG: 유령 방향 이동\n");
        
        return result.toString();
    }
    
    // 스테이지 타입에 따른 심볼 반환
    private String getStageSymbol(Stage stage) {
        if (stage instanceof NormalStage) return "N";
        if (stage instanceof EventStage) return "E";
        if (stage instanceof GhostStage) return "G";
        if (stage instanceof BuffStage) return "B";
        if (stage instanceof ForceMove) {
            ForceMove fm = (ForceMove) stage;
            if (fm.getForceStage() > 0) return "F+";
            return "F-";
        }
        return "?";
    }
} 