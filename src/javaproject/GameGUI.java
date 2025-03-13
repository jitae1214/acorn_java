package javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/*
주요 기능:
- 게임의 그래픽 인터페이스를 담당
- 보드 게임판, 주사위, 상태 표시 등을 시각적으로 구현
- 게임 진행 상황을 실시간으로 업데이트

개선 가능한 부분:
- 화면 크기 조절 시 레이아웃 자동 조정 기능 추가 필요
- 애니메이션 효과 더 부드럽게 개선 가능
- 사운드 효과 구현 필요
*/

// JFrame은 Java Swing에서 제공하는 가장 기본적인 윈도우(창) 컨테이너
public class GameGUI extends JFrame {
    private GameMaster game;
    private JPanel boardPanel;
    private JButton diceButton;
    private JLabel statusLabel;
    private JLabel buffLabel;
    private JLabel locationLabel;
    private static int SQUARE_SIZE = 60;
    private static final int BOARD_SIZE = 30;
    private Random random = new Random();

    /*
     * 생성자: GameMaster 인스턴스 생성 및 UI 초기화
     * 콘솔 버전의 main 메소드를 대체
     */
    public GameGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setGameMaster(GameMaster gameMaster) {
        this.game = gameMaster;
        initializeUI();  // UI 초기화는 GameMaster가 설정된 후에 실행
    }

    /*
     * UI 초기화 메소드
     * - 상단: 게임 상태 표시 (위치, 버프 등)
     * - 중앙: 보드 표시
     * - 하단: 주사위 버튼
     */
    private void initializeUI() {
        setTitle("유령 피하기 보드게임");
        setLayout(new BorderLayout());

        // 전체 화면 설정
        setExtendedState(JFrame.MAXIMIZED_BOTH); //창 전체 화면
        setUndecorated(true); // 창 테두리 제거

        // ESC 키로 전체 화면 종료 가능하게 설정
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

        // 상태 패널 (상단) - 폰트 크기 증가
        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font largeFont = new Font("맑은 고딕", Font.BOLD, 24);
        statusLabel = new JLabel("게임 시작!", SwingConstants.CENTER);
        buffLabel = new JLabel("현재 버프: normal", SwingConstants.CENTER);
        locationLabel = new JLabel("현재 위치: 1번 칸", SwingConstants.CENTER);
        
        statusLabel.setFont(largeFont);
        buffLabel.setFont(largeFont);
        locationLabel.setFont(largeFont);

        statusPanel.add(statusLabel);
        statusPanel.add(buffLabel);
        statusPanel.add(locationLabel);
        add(statusPanel, BorderLayout.NORTH);

        // 보드 패널 (중앙) - 크기 조정
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setBackground(Color.WHITE);
        // 화면 크기에 맞게 보드 크기 조정
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int boardWidth = (int)(screenSize.width * 0.8);
        int boardHeight = (int)(screenSize.height * 0.6);
        boardPanel.setPreferredSize(new Dimension(boardWidth, boardHeight));
        add(new JScrollPane(boardPanel), BorderLayout.CENTER);

        // 주사위 버튼 패널 (하단)
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        diceButton = new JButton("주사위 굴리기");
        diceButton.setFont(largeFont);
        diceButton.setPreferredSize(new Dimension(200, 60));
        diceButton.addActionListener(e -> rollDice());
        
        controlPanel.add(diceButton);
        add(controlPanel, BorderLayout.SOUTH);

        // 전체 화면 크기에 맞게 SQUARE_SIZE 조정
        SQUARE_SIZE = Math.min(boardWidth / 11, boardHeight / 5);
    }

    /*
     * 보드 그리기 메소드
     * 콘솔의 문자 기반 출력을 그래픽으로 변경
     * - 각 칸을 사각형으로 표시
     * - 플레이어와 유령을 원으로 표시
     * - 칸 번호 표시
     */
    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        ArrayList<Stage> board = game.getBoard();
        int margin = SQUARE_SIZE / 2;
        int x = margin;
        int y = margin;
        int row = 0;

        Font boardFont = new Font("맑은 고딕", Font.BOLD, SQUARE_SIZE / 4);
        g2d.setFont(boardFont);

        for (int i = 0; i < BOARD_SIZE; i++) {
            // 새로운 줄 시작
            if (i > 0 && i % 10 == 0) {
                row++;
                x = margin;
                y = margin + (row * (SQUARE_SIZE + margin));
            }

            // 칸 그리기 (그림자 효과 추가)
            g2d.setColor(new Color(180, 180, 180));
            g2d.fillRect(x + 3, y + 3, SQUARE_SIZE, SQUARE_SIZE);
            g2d.setColor(getSquareColor(board.get(i)));
            g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);

            // 칸 번호 표시
            String number = String.valueOf(i + 1);
            FontMetrics metrics = g2d.getFontMetrics();
            int numberX = x + (SQUARE_SIZE - metrics.stringWidth(number)) / 2;
            int numberY = y + metrics.getHeight();
            g2d.drawString(number, numberX, numberY);

            // 플레이어 위치 표시 (그라데이션 효과 추가)
            if (i == game.getUserLoc()) {
                GradientPaint gradient = new GradientPaint(
                    x + SQUARE_SIZE/4, y + SQUARE_SIZE/2,
                    Color.YELLOW,
                    x + SQUARE_SIZE*3/4, y + SQUARE_SIZE*3/4,
                    new Color(255, 200, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillOval(x + SQUARE_SIZE/4, y + SQUARE_SIZE/2, SQUARE_SIZE/2, SQUARE_SIZE/2);
            }


            // 유령 위치 표시 (그라데이션 효과 추가)
            if (i == game.getGhostLoc()) {
                GradientPaint gradient = new GradientPaint(
                    x + SQUARE_SIZE/4, y + SQUARE_SIZE/2,
                    Color.RED,
                    x + SQUARE_SIZE*3/4, y + SQUARE_SIZE*3/4,
                    new Color(180, 0, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillOval(x + SQUARE_SIZE/4, y + SQUARE_SIZE/2, SQUARE_SIZE/2, SQUARE_SIZE/2);
            }

            x += SQUARE_SIZE + margin;
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
        Timer timer = new Timer(50, new ActionListener() {
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