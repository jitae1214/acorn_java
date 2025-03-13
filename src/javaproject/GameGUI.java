package javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/*
  1. GUI 클래스 추가
  2. 콘솔 버전에서 그래픽 인터페이스로 변경하기 위한 메인 클래스
*/

// JFrame은 Java Swing에서 제공하는 가장 기본적인 윈도우(창) 컨테이너
public class GameGUI extends JFrame {
    private GameMaster game;
    private JPanel boardPanel;
    private JButton diceButton;
    private JLabel statusLabel;
    private JLabel buffLabel;
    private JLabel locationLabel;
    private static final int SQUARE_SIZE = 60;
    private static final int BOARD_SIZE = 30;
    private Random random = new Random();

    /*
     * 생성자: GameMaster 인스턴스 생성 및 UI 초기화
     * 콘솔 버전의 main 메소드를 대체
     */
    public GameGUI() {
        game = new GameMaster();
        initializeUI();
    }

    /*
     * UI 초기화 메소드
     * - 상단: 게임 상태 표시 (위치, 버프 등)
     * - 중앙: 보드 표시
     * - 하단: 주사위 버튼
     */
    private void initializeUI() {
        setTitle("유령 피하기 보드게임"); // 창 제목 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창이 닫힐 때의 동작 설정
        setLayout(new BorderLayout());// 레이아웃 설정

        // 상태 패널 (상단)
        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        statusLabel = new JLabel("게임 시작!", SwingConstants.CENTER);
        buffLabel = new JLabel("현재 버프: normal", SwingConstants.CENTER);
        locationLabel = new JLabel("현재 위치: 1번 칸", SwingConstants.CENTER);
        statusPanel.add(statusLabel);
        statusPanel.add(buffLabel);
        statusPanel.add(locationLabel);
        add(statusPanel, BorderLayout.NORTH);

        // 보드 패널 (중앙)
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(SQUARE_SIZE * 10, SQUARE_SIZE * 4));
        add(new JScrollPane(boardPanel), BorderLayout.CENTER);

        // 주사위 버튼 패널 (하단)
        JPanel controlPanel = new JPanel();
        diceButton = new JButton("주사위 굴리기");
        diceButton.addActionListener(e -> rollDice());
        controlPanel.add(diceButton);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /*
     * 보드 그리기 메소드
     * 콘솔의 문자 기반 출력을 그래픽으로 변경
     * - 각 칸을 사각형으로 표시
     * - 플레이어와 유령을 원으로 표시
     * - 칸 번호 표시
     */
    private void drawBoard(Graphics g) {
        ArrayList<Stage> board = game.getBoard();
        int x = 10;
        int y = 10;
        int row = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            // 새로운 줄 시작
            if (i > 0 && i % 10 == 0) {
                row++;
                x = 10;
                y = 10 + (row * (SQUARE_SIZE + 10));
            }

            // 칸 그리기
            g.setColor(getSquareColor(board.get(i)));
            g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);

            // 칸 번호 표시
            g.drawString(String.valueOf(i + 1), x + 5, y + 20);

            // 플레이어 위치 표시
            if (i == game.getUserLoc()) {
                g.setColor(Color.YELLOW);
                g.fillOval(x + 10, y + 25, 20, 20);
            }

            // 유령 위치 표시
            if (i == game.getGhostLoc()) {
                g.setColor(Color.RED);
                g.fillOval(x + 30, y + 25, 20, 20);
            }

            x += SQUARE_SIZE + 10;
        }
    }

    /*
     * 칸 색상 결정 메소드
     * 콘솔의 텍스트 색상을 실제 색상으로 변경
     */
    private Color getSquareColor(Stage stage) {
        if (stage instanceof NormalStage) return Color.WHITE;
        if (stage instanceof EventStage) return new Color(173, 216, 230); // 연한 파란색
        if (stage instanceof GhostStage) return new Color(255, 182, 193); // 연한 빨간색
        if (stage instanceof BuffStage) return new Color(144, 238, 144);  // 연한 초록색
        if (stage instanceof ForceMove) return new Color(221, 160, 221);  // 연한 보라색
        return Color.GRAY;
    }

    /*
     * 주사위 굴리기 메소드
     * 콘솔의 즉시 결과 출력을 애니메이션 효과로 변경
     * - 주사위 굴리는 애니메이션 추가
     * - 결과에 따른 자동 이동
     * - UI 자동 업데이트
     */
    private void rollDice() {
        diceButton.setEnabled(false);
        
        // 주사위 굴리기 효과를 위한 타이머
        Timer timer = new Timer(100, new ActionListener() {
            int count = 0;
            int finalResult = game.diceRoll();
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count < 10) {
                    statusLabel.setText("주사위 굴리는 중... " + (random.nextInt(6) + 1));
                    count++;
                } else {
                    ((Timer)e.getSource()).stop();
                    statusLabel.setText("주사위 결과: " + finalResult);
                    
                    // 플레이어 이동
                    game.userMove(finalResult);
                    
                    // UI 업데이트
                    updateUI();
                    
                    // 게임 종료 체크
                    if (game.checkGoal()) {
                        JOptionPane.showMessageDialog(GameGUI.this, 
                            "축하합니다! 게임을 클리어하셨습니다!", 
                            "게임 종료", 
                            JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
                    
                    diceButton.setEnabled(true);
                }
            }
        });
        
        timer.start();
    }

    /*
     * UI 업데이트 메소드
     * 콘솔의 전체 화면 갱신을 부분 업데이트로 변경
     */
    private void updateUI() {
        buffLabel.setText("현재 버프: " + game.getBuff());
        locationLabel.setText("현재 위치: " + (game.getUserLoc() + 1) + "번 칸");
        boardPanel.repaint();
    }

    /*
     * 메인 메소드
     * Swing 이벤트 디스패치 스레드에서 GUI 실행
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameGUI().setVisible(true);
        });
    }
} 