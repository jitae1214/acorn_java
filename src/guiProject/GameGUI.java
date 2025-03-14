package guiProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        
        // ESC 키로 종료 기능 추가
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

        // 메인 패널 생성
        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);
        
        // 보드 패널 설정
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setBackground(new Color(20, 20, 30));
        boardPanel.setOpaque(true);
        
        // 상태 정보 레이블 설정
        statusLabel = new JLabel("게임 시작!", SwingConstants.LEFT);
        buffLabel = new JLabel("현재 버프: normal", SwingConstants.LEFT);
        locationLabel = new JLabel("현재 위치: 1번 칸", SwingConstants.LEFT);
        
        Font statusFont = new Font("맑은 고딕", Font.BOLD, 16);
        statusLabel.setFont(statusFont);
        buffLabel.setFont(statusFont);
        locationLabel.setFont(statusFont);
        
        statusLabel.setForeground(Color.WHITE);
        buffLabel.setForeground(Color.WHITE);
        locationLabel.setForeground(Color.WHITE);

        // 상태 패널 설정
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);
        statusPanel.add(statusLabel);
        statusPanel.add(buffLabel);
        statusPanel.add(locationLabel);
        
        // 주사위 버튼 설정
        diceButton = new JButton("주사위 굴리기");
        diceButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        diceButton.setPreferredSize(new Dimension(150, 50));
        diceButton.addActionListener(e -> rollDice());

        // 각 화면에 필요한 ui의 크기와 위치 설정
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                Dimension size = getContentPane().getSize();
                
                // 보드 패널을 전체 화면으로 설정
                boardPanel.setBounds(0, 0, size.width, size.height);
                
                // 상태 패널 위치 (좌상단)
                statusPanel.setBounds(20, 20, 200, 100);
                
                // 주사위 버튼 위치 (우하단)
                diceButton.setBounds(size.width - 170, size.height - 70, 150, 50);
                
                // SQUARE_SIZE 조정
                SQUARE_SIZE = Math.min(size.width / 15, size.height / 7);
            }
        });

        // 레이어드 패널에 컴포넌트 추가
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(statusPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(diceButton, JLayeredPane.PALETTE_LAYER);
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
        
        // 미로 스타일의 레이아웃을 위한 설정
        int cols = 6; // 가로 6칸
        int rows = 5; // 세로 5칸
        int startX = (boardPanel.getWidth() - (cols * (SQUARE_SIZE + margin))) / 2;
        int startY = (boardPanel.getHeight() - (rows * (SQUARE_SIZE + margin))) / 2;

        Font boardFont = new Font("맑은 고딕", Font.BOLD, SQUARE_SIZE / 4);
        g2d.setFont(boardFont);

        // 미로 연결선 그리기
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(3));
        drawMazeConnections(g2d, startX, startY, margin);

        // 각 칸 그리기
        for (int i = 0; i < BOARD_SIZE; i++) {
            int row = i / cols;
            int col = i % cols;
            int x = startX + col * (SQUARE_SIZE + margin);
            int y = startY + row * (SQUARE_SIZE + margin);

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

            // 플레이어 위치 표시 (캐릭터 형태로 변경)
            if (i == game.getUserLoc()) {
                drawPlayer(g2d, x + SQUARE_SIZE/4, y + SQUARE_SIZE/4, SQUARE_SIZE/2);
            }

            // 유령 위치 표시 (귀여운 유령 형태로 변경)
            if (i == game.getGhostLoc()) {
                drawGhost(g2d, x + SQUARE_SIZE/4, y + SQUARE_SIZE/4, SQUARE_SIZE/2);
            }
        }
    }

    // 미로 연결선 그리기 메서드 추가
    private void drawMazeConnections(Graphics2D g2d, int startX, int startY, int margin) {
        // 미로 연결선 패턴 정의 (1은 연결, 0은 벽)
        int[][] horizontalConnections = {
            {1, 1, 1, 0, 1}, // 1행
            {0, 1, 1, 1, 0}, // 2행
            {1, 0, 1, 1, 1}, // 3행
            {1, 1, 0, 1, 1}, // 4행
            {1, 1, 1, 0, 1}  // 5행
        };
        
        int[][] verticalConnections = {
            {1, 0, 1, 1, 0, 1}, // 1열
            {1, 1, 0, 1, 1, 0}, // 2열
            {0, 1, 1, 0, 1, 1}, // 3열
            {1, 0, 1, 1, 0, 1}, // 4열
        };

        // 가로 연결선 그리기
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (horizontalConnections[row][col] == 1) {
                    int x1 = startX + col * (SQUARE_SIZE + margin) + SQUARE_SIZE;
                    int y1 = startY + row * (SQUARE_SIZE + margin) + SQUARE_SIZE/2;
                    g2d.drawLine(x1, y1, x1 + margin, y1);
                }
            }
        }

        // 세로 연결선 그리기
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 6; col++) {
                if (verticalConnections[row][col] == 1) {
                    int x1 = startX + col * (SQUARE_SIZE + margin) + SQUARE_SIZE/2;
                    int y1 = startY + row * (SQUARE_SIZE + margin) + SQUARE_SIZE;
                    g2d.drawLine(x1, y1, x1, y1 + margin);
                }
            }
        }
    }

    // 플레이어 캐릭터 그리기 메서드 추가
    private void drawPlayer(Graphics2D g2d, int x, int y, int size) {
        // 원본 설정 저장
        Composite originalComposite = g2d.getComposite();
        Stroke originalStroke = g2d.getStroke();

        // 캐릭터 몸체 (동그란 모자를 쓴 형태)
        g2d.setColor(new Color(255, 220, 0)); // 노란색 계열
        g2d.fillOval(x, y + size/4, size, size/2); // 몸체

        // 모자 그리기
        g2d.setColor(new Color(50, 50, 255)); // 파란색 모자
        g2d.fillArc(x - size/8, y, size + size/4, size/2, 0, 180);

        // 얼굴 특징
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        // 눈
        g2d.fillOval(x + size/3, y + size/3, size/6, size/6);
        g2d.fillOval(x + size*2/3, y + size/3, size/6, size/6);
        
        // 웃는 입
        g2d.drawArc(x + size/3, y + size/3, size/2, size/3, 0, -180);

        // 설정 복구
        g2d.setComposite(originalComposite);
        g2d.setStroke(originalStroke);
    }

    // 유령 캐릭터 그리기 메서드 추가
    private void drawGhost(Graphics2D g2d, int x, int y, int size) {
        // 원본 설정 저장
        Composite originalComposite = g2d.getComposite();
        Stroke originalStroke = g2d.getStroke();

        // 유령 몸체 (반투명 효과)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        
        // 그라데이션 효과
        GradientPaint ghostGradient = new GradientPaint(
            x, y, new Color(255, 100, 100),
            x + size, y + size, new Color(200, 0, 0)
        );
        g2d.setPaint(ghostGradient);

        // 유령 몸체 (둥근 상단과 물결무늬 하단)
        int[] xPoints = new int[]{
            x, x + size,
            x + size, x + size*4/5, x + size*3/5, x + size*2/5, x + size/5, x
        };
        int[] yPoints = new int[]{
            y + size/3, y + size/3,
            y + size, y + size*4/5, y + size, y + size*4/5, y + size, y + size
        };
        
        // 상단 부분 (둥근 형태)
        g2d.fillArc(x, y, size, size*2/3, 0, 180);
        // 하단 부분 (물결 모양)
        g2d.fillPolygon(xPoints, yPoints, xPoints.length);

        // 눈 (빛나는 효과)
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x + size/4 - 2, y + size/3 - 2, size/4 + 4, size/4 + 4);
        g2d.fillOval(x + size*2/3 - 2, y + size/3 - 2, size/4 + 4, size/4 + 4);

        g2d.setColor(new Color(50, 0, 0));
        g2d.fillOval(x + size/4, y + size/3, size/4, size/4);
        g2d.fillOval(x + size*2/3, y + size/3, size/4, size/4);

        // 설정 복구
        g2d.setComposite(originalComposite);
        g2d.setStroke(originalStroke);
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