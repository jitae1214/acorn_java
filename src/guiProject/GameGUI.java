package guiProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.awt.image.BufferedImage;

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
    // 게임의 전반적인 로직과 상태를 관리하는 GameMaster 인스턴스
    private GameMaster game;
    
    // 게임 보드를 표시하는 패널
    private JPanel boardPanel;
    
    // 주사위 굴리기 기능을 제공하는 버튼
    private JButton diceButton;
    
    // 현재 게임 상태(턴, 이벤트 등)를 표시하는 레이블
    private JLabel statusLabel;
    
    // 현재 적용된 버프 효과를 표시하는 레이블
    private JLabel buffLabel;
    
    // 플레이어의 현재 위치를 표시하는 레이블
    private JLabel locationLabel;
    
    // 보드판의 각 칸의 크기를 정의하는 상수 (픽셀 단위)
    private static int SQUARE_SIZE = 60;
    
    // 보드판의 전체 칸 수를 정의하는 상수
    private static final int BOARD_SIZE = 30;
    
    // 주사위 굴리기에 사용되는 난수 생성기
    private Random random = new Random();
    
    // 주사위 관련 필드 추가
    private JLabel diceLabel;
    private Timer diceAnimationTimer;
    private int diceAnimationCount = 0;
    private int finalDiceResult = 0;
    private ImageIcon[] diceImages;
    private static final int DICE_SIZE = 100;
    private static final int ANIMATION_FRAMES = 10;

    /*
     * 생성자: GameMaster 인스턴스 생성 및 UI 초기화
     * 콘솔 버전의 main 메소드를 대체
     */
    public GameGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadDiceImages();
    }

    /**
     * 주사위 이미지를 로드하는 메소드
     * 실제 이미지 파일이 없는 경우 기본 주사위 이미지를 생성하여 사용
     */
    private void loadDiceImages() {
        // 6면 주사위 이미지를 저장할 배열 초기화
        diceImages = new ImageIcon[6];
        
        // 1부터 6까지의 주사위 면에 대한 이미지 로드
        for (int i = 1; i <= 6; i++) {
            try {
                // 주사위 이미지 파일 경로 설정
                String imagePath = "/Users/hyunki/Desktop/Test2/src/resources/dice" + i + ".png";
                File file = new File(imagePath);
                
                if (file.exists()) {
                    // 이미지 파일이 존재하는 경우
                    ImageIcon originalIcon = new ImageIcon(imagePath);
                    // 이미지 크기를 DICE_SIZE에 맞게 조정 (부드러운 스케일링 적용)
                    Image image = originalIcon.getImage().getScaledInstance(DICE_SIZE, DICE_SIZE, Image.SCALE_SMOOTH);
                    diceImages[i-1] = new ImageIcon(image);
                } else {
                    // 이미지 파일이 없는 경우 에러 메시지 출력
                    System.err.println("주사위 이미지 파일을 찾을 수 없습니다: " + imagePath);
                    // 기본 주사위 이미지 생성하여 사용
                    BufferedImage defaultDice = createDefaultDiceImage(i);
                    diceImages[i-1] = new ImageIcon(defaultDice.getScaledInstance(DICE_SIZE, DICE_SIZE, Image.SCALE_SMOOTH));
                }
            } catch (Exception e) {
                // 이미지 로드 중 예외 발생 시 에러 메시지 출력
                System.err.println("주사위 이미지 로드 실패: " + e.getMessage());
                // 예외 발생 시에도 기본 주사위 이미지 생성하여 사용
                BufferedImage defaultDice = createDefaultDiceImage(i);
                diceImages[i-1] = new ImageIcon(defaultDice.getScaledInstance(DICE_SIZE, DICE_SIZE, Image.SCALE_SMOOTH));
            }
        }
    }

    // 기본 주사위 이미지를 생성하는 메서드 추가
    private BufferedImage createDefaultDiceImage(int number) {
        BufferedImage image = new BufferedImage(DICE_SIZE, DICE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // 안티앨리어싱(선명하게 계단효과 제거) 설정
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 주사위 배경
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(0, 0, DICE_SIZE-1, DICE_SIZE-1, 10, 10);
        
        // 테두리
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, DICE_SIZE-1, DICE_SIZE-1, 10, 10);
        
        // 숫자 그리기
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, DICE_SIZE/2));
        FontMetrics fm = g2d.getFontMetrics();
        String num = String.valueOf(number);
        int textX = (DICE_SIZE - fm.stringWidth(num)) / 2;
        int textY = (DICE_SIZE - fm.getHeight()) / 2 + fm.getAscent();
        
        g2d.drawString(num, textX, textY);
        
        g2d.dispose();
        return image;
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

        // 범례 패널 추가
        JPanel legendPanel = createLegendPanel();
        
        // 주사위 버튼 설정
        diceButton = new JButton("주사위 굴리기") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 버튼 배경
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(60, 0, 0),
                    0, getHeight(), new Color(120, 0, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // 테두리
                g2d.setColor(new Color(200, 0, 0));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);

                // 텍스트
                g2d.setFont(new Font("맑은 고딕", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                // 텍스트 그림자
                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.drawString(getText(), textX+1, textY+1);

                // 텍스트
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), textX, textY);
            }
        };
        diceButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        diceButton.setForeground(Color.WHITE);
        diceButton.setPreferredSize(new Dimension(150, 50));
        diceButton.setContentAreaFilled(false);
        diceButton.setBorderPainted(false);
        diceButton.setFocusPainted(false);
        diceButton.addActionListener(e -> rollDice());

        // 주사위 레이블 설정
        diceLabel = new JLabel();
        diceLabel.setPreferredSize(new Dimension(DICE_SIZE, DICE_SIZE));
        diceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        diceLabel.setOpaque(true);
        diceLabel.setBackground(new Color(20, 20, 30));
        if (diceImages[0] != null) {
            diceLabel.setIcon(diceImages[0]);
        }

        // 주사위 패널 설정
        JPanel dicePanel = new JPanel();
        dicePanel.setLayout(new BoxLayout(dicePanel, BoxLayout.Y_AXIS));
        dicePanel.setBackground(new Color(20, 20, 30));
        dicePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 주사위 이미지를 가운데 정렬
        JPanel diceImagePanel = new JPanel();
        diceImagePanel.setBackground(new Color(20, 20, 30));
        diceImagePanel.add(diceLabel);
        
        // 주사위 버튼을 가운데 정렬
        JPanel diceButtonPanel = new JPanel();
        diceButtonPanel.setBackground(new Color(20, 20, 30));
        diceButtonPanel.add(diceButton);

        dicePanel.add(diceImagePanel);
        dicePanel.add(Box.createVerticalStrut(10));
        dicePanel.add(diceButtonPanel);

        // 주사위 패널 위치 설정
        Dimension size = getContentPane().getSize();
        dicePanel.setBounds(20, size.height - 250, DICE_SIZE + 40, DICE_SIZE + 100);

        // 각 화면에 필요한 ui의 크기와 위치 설정
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                Dimension size = getContentPane().getSize();
                
                // 보드 패널을 전체 화면으로 설정
                boardPanel.setBounds(0, 0, size.width, size.height);
                
                // 상태 패널 위치 (좌상단)
                statusPanel.setBounds(20, 20, 200, 100);
                
                // 범례 패널 위치 (우측)
                legendPanel.setBounds(size.width - 250, 20, 230, size.height - 100);
                
                // 주사위 패널 위치 (좌하단)
                dicePanel.setBounds(20, size.height - 250, DICE_SIZE + 40, DICE_SIZE + 100);
                
                // SQUARE_SIZE 조정
                SQUARE_SIZE = Math.min(size.width / 15, size.height / 7);
            }
        });

        // 레이어드 패널에 컴포넌트 추가
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(statusPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(legendPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(dicePanel, JLayeredPane.PALETTE_LAYER);

        // 주사위 버튼에 마우스 이벤트 추가
        diceButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                diceButton.setFont(new Font("맑은 고딕", Font.BOLD, 22));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                diceButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
            }
        });
    }

    // 범례 패널 생성 메서드
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(new Color(20, 20, 30, 200));
        legendPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 범례 제목
        JLabel titleLabel = new JLabel("[ 칸 설명 ]");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(titleLabel);
        legendPanel.add(Box.createVerticalStrut(15));

        // 범례 항목 추가
        addLegendItem(legendPanel, "S", new Color(0, 100, 0), "시작 칸");
        addLegendItem(legendPanel, "G", new Color(0, 100, 0), "골인 칸");
        addLegendItem(legendPanel, "?", new Color(0, 0, 150), "이벤트 칸 - 퀴즈를 풀어야 합니다");
        addLegendItem(legendPanel, "☠", new Color(150, 0, 0), "유령 칸 - 유령이 이동합니다");
        addLegendItem(legendPanel, "★", new Color(0, 150, 0), "버프 칸 - 이동 효과가 변경됩니다");
        addLegendItem(legendPanel, "→", new Color(150, 0, 150), "앞으로 이동");
        addLegendItem(legendPanel, "←", new Color(150, 0, 150), "뒤로 이동");
        addLegendItem(legendPanel, "⇝", new Color(150, 0, 150), "유령 방향으로 이동");

        return legendPanel;
    }

    // 범례 항목 추가 메서드
    private void addLegendItem(JPanel panel, String symbol, Color symbolColor, String description) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
        itemPanel.setBackground(new Color(0, 0, 0, 0));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 심볼(칸 마다 알맞게 직관적으로 표시 하기)
        JLabel symbolLabel = new JLabel(symbol);
        symbolLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        symbolLabel.setForeground(symbolColor);
        symbolLabel.setPreferredSize(new Dimension(40, 30));

        // 설명
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        descLabel.setForeground(Color.WHITE);

        itemPanel.add(symbolLabel);
        itemPanel.add(Box.createHorizontalStrut(10));
        itemPanel.add(descLabel);

        panel.add(itemPanel);
        panel.add(Box.createVerticalStrut(10));
    }

    // 게임 보드를 그리는 메서드: 보드의 모든 시각적 요소를 렌더링
    private void drawBoard(Graphics g) {
        // 안티앨리어싱을 적용하여 더 부드러운 그래픽 표현을 위한 2D 그래픽스 설정
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 게임 보드의 현재 상태 가져오기
        ArrayList<Stage> board = game.getBoard();
        int margin = SQUARE_SIZE / 2;  // 각 칸 사이의 여백 설정
        
        // 미로 스타일의 레이아웃을 위한 그리드 설정
        int cols = 6; // 가로 6칸
        int rows = 5; // 세로 5칸
        // 보드를 화면 중앙에 배치하기 위한 시작 좌표 계산
        int startX = (boardPanel.getWidth() - (cols * (SQUARE_SIZE + margin))) / 2;
        int startY = (boardPanel.getHeight() - (rows * (SQUARE_SIZE + margin))) / 2;

        // 보드에 사용될 폰트 설정
        Font boardFont = new Font("맑은 고딕", Font.BOLD, SQUARE_SIZE / 4);
        g2d.setFont(boardFont);

        // 미로의 연결선 그리기 (칸과 칸 사이의 경로)
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(3));
        drawMazeConnections(g2d, startX, startY, margin);

        // 보드의 각 칸을 순회하며 그리기
        for (int i = 0; i < BOARD_SIZE; i++) {
            // 현재 칸의 행과 열 위치 계산
            int row = i / cols;
            int col = i % cols;
            // 현재 칸의 실제 x, y 좌표 계산
            int x = startX + col * (SQUARE_SIZE + margin);
            int y = startY + row * (SQUARE_SIZE + margin);

            // 각 칸 그리기 (그림자 효과 포함)
            g2d.setColor(new Color(180, 180, 180));  // 그림자 색상
            g2d.fillRect(x + 3, y + 3, SQUARE_SIZE, SQUARE_SIZE);  // 그림자
            g2d.setColor(getSquareColor(board.get(i)));  // 칸의 메인 색상
            g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);  // 메인 사각형
            g2d.setColor(Color.BLACK);  // 테두리 색상
            g2d.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);  // 테두리

            // 각 칸의 번호 표시
            String number = String.valueOf(i + 1);
            FontMetrics metrics = g2d.getFontMetrics();
            // 번호를 칸의 중앙에 배치하기 위한 좌표 계산
            int numberX = x + (SQUARE_SIZE - metrics.stringWidth(number)) / 2;
            int numberY = y + metrics.getHeight();
            g2d.drawString(number, numberX, numberY);

            // 각 칸의 특수 심볼 그리기 (함정, 아이템 등)
            drawSquareSymbol(g2d, board.get(i), x, y, i);

            // 플레이어와 유령의 현재 위치 표시
            if (i == game.getUserLoc() && i == game.getGhostLoc()) {
                // 플레이어와 유령이 같은 칸에 있는 경우
                drawPlayer(g2d, x + SQUARE_SIZE/4, y + SQUARE_SIZE/4, SQUARE_SIZE/2);
                drawGhost(g2d, x + SQUARE_SIZE/4, y + SQUARE_SIZE/4, SQUARE_SIZE/2);
            } else if (i == game.getUserLoc()) {
                // 플레이어만 있는 경우
                drawPlayer(g2d, x + SQUARE_SIZE/4, y + SQUARE_SIZE/4, SQUARE_SIZE/2);
            } else if (i == game.getGhostLoc()) {
                // 유령만 있는 경우
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
        finalDiceResult = game.diceRoll();
        diceAnimationCount = 0;

        // 주사위 애니메이션 타이머 설정
        if (diceAnimationTimer != null) {
            diceAnimationTimer.stop();
        }

        diceAnimationTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (diceAnimationCount < ANIMATION_FRAMES) {
                    // 랜덤한 주사위 이미지 표시
                    int randomDice = random.nextInt(6);
                    if (diceImages[randomDice] != null) {
                        diceLabel.setIcon(diceImages[randomDice]);
                    }
                    diceAnimationCount++;
                } else {
                    // 최종 주사위 결과 표시
                    if (diceImages[finalDiceResult - 1] != null) {
                        diceLabel.setIcon(diceImages[finalDiceResult - 1]);
                    }
                    diceAnimationTimer.stop();
                    
                    // 플레이어 이동
                    game.userMove(finalDiceResult);
                    
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
        
        diceAnimationTimer.start();
    }

    /*
     * UI 업데이트 메소드
     * 콘솔의 전체 화면 갱신을 부분 업데이트로 변경
     */
    public void updateUI() {
        buffLabel.setText("현재 버프: " + game.getBuff());
        locationLabel.setText("현재 위치: " + (game.getUserLoc() + 1) + "번 칸");
        boardPanel.repaint();
    }

    // 퀴즈 다이얼로그 표시 메소드
    public void showQuizDialog(Quiz quiz) {
        JDialog dialog = new JDialog(this, "퀴즈!", true);
        dialog.setLayout(new BorderLayout());
        dialog.setUndecorated(true);

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(20, 20, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 퀴즈 내용
        JLabel questionLabel = new JLabel("<html><body style='width: 400px'>" + quiz.getContent() + "</body></html>");
        questionLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(questionLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // 답변 입력 컴포넌트
        JComponent answerComponent;
        String example = quiz.getExample();

        if (example.contains("/")) {
            // 객관식 문제
            answerComponent = new JPanel();
            ((JPanel)answerComponent).setLayout(new BoxLayout((JPanel)answerComponent, BoxLayout.Y_AXIS));
            answerComponent.setBackground(new Color(20, 20, 30));
            
            ButtonGroup group = new ButtonGroup();
            ArrayList<JRadioButton> radioButtons = new ArrayList<>();

            String[] options = example.split("/");
            for (String option : options) {
                String trimmedOption = option.trim();
                if (!trimmedOption.isEmpty()) {
                    JRadioButton radioButton = new JRadioButton(trimmedOption);
                    radioButton.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
                    radioButton.setForeground(Color.WHITE);
                    radioButton.setBackground(new Color(20, 20, 30));
                    
                    // 라디오 버튼 패널 생성
                    JPanel radioPanel = new JPanel(new BorderLayout());
                    radioPanel.setBackground(new Color(20, 20, 30));
                    radioPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    radioPanel.add(radioButton, BorderLayout.WEST);
                    
                    group.add(radioButton);
                    radioButtons.add(radioButton);
                    answerComponent.add(radioPanel);
                    answerComponent.add(Box.createVerticalStrut(10));
                }
            }

            // 첫 번째 라디오 버튼 선택
            if (!radioButtons.isEmpty()) {
                radioButtons.get(0).setSelected(true);
            }
        } else {
            // 서술형 문제
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
            inputPanel.setBackground(new Color(20, 20, 30));

            // 답안 입력 필드
            JTextField textField = new JTextField();
            textField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            textField.setPreferredSize(new Dimension(300, 30));
            textField.setMaximumSize(new Dimension(300, 30));
            
            // 힌트 레이블 (있는 경우)
            if (!example.isEmpty()) {
                JLabel hintLabel = new JLabel("힌트: " + example);
                hintLabel.setFont(new Font("맑은 고딕", Font.ITALIC, 14));
                hintLabel.setForeground(new Color(200, 200, 200));
                hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                inputPanel.add(hintLabel);
                inputPanel.add(Box.createVerticalStrut(10));
            }
            
            inputPanel.add(textField);
            answerComponent = inputPanel;
        }

        mainPanel.add(answerComponent);
        mainPanel.add(Box.createVerticalStrut(20));

        // 확인 버튼
        JButton submitButton = new JButton("정답 제출");
        submitButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            String userAnswer;
            if (answerComponent instanceof JPanel && ((JPanel)answerComponent).getComponent(0) instanceof JPanel) {
                // 객관식 답변 처리
                for (Component c : ((JPanel)answerComponent).getComponents()) {
                    if (c instanceof JPanel) {
                        JRadioButton rb = (JRadioButton)((JPanel)c).getComponent(0);
                        if (rb.isSelected()) {
                            userAnswer = rb.getText();
                            boolean correct = quiz.isCorrect(userAnswer);
                            dialog.dispose();
                            
                            if (correct) {
                                showResultDialog("정답입니다!", true);
                            } else {
                                showResultDialog("오답입니다...", false);
                                game.ghostMove(3);
                            }
                            break;
                        }
                    }
                }
            } else {
                // 서술형 답변 처리
                JTextField textField = (JTextField)((JPanel)answerComponent).getComponent(
                    ((JPanel)answerComponent).getComponentCount() - 1);
                userAnswer = textField.getText().trim();
                boolean correct = quiz.isCorrect(userAnswer);
                dialog.dispose();
                
                if (correct) {
                    showResultDialog("정답입니다!", true);
                } else {
                    showResultDialog("오답입니다...", false);
                    game.ghostMove(3);
                }
            }
        });

        mainPanel.add(submitButton);
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 퀴즈 결과 다이얼로그
    private void showResultDialog(String message, boolean isCorrect) {
        JDialog resultDialog = new JDialog(this, "", true);
        resultDialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(isCorrect ? new Color(0, 100, 0) : new Color(100, 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        resultDialog.add(panel);
        resultDialog.pack();
        resultDialog.setLocationRelativeTo(this);
        
        // 3초 후 자동으로 닫기
        Timer timer = new Timer(3000, e -> resultDialog.dispose());
        timer.setRepeats(false);
        timer.start();
        
        resultDialog.setVisible(true);
    }

    // 게임 오버 다이얼로그 표시
    public void showGameOver(String message) {
        JDialog dialog = new JDialog(this, "게임 오버", true);
        dialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(100, 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        // 3초 후 게임 종료
        Timer timer = new Timer(3000, e -> {
            dialog.dispose();
            System.exit(0);
        });
        timer.setRepeats(false);
        timer.start();
    }

    // 각 칸의 심볼을 그리는 메서드
    private void drawSquareSymbol(Graphics2D g2d, Stage stage, int x, int y, int index) {
        int symbolSize = SQUARE_SIZE / 3;
        int symbolX = x + (SQUARE_SIZE - symbolSize) / 2;
        int symbolY = y + SQUARE_SIZE / 2;
        
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, symbolSize));
        
        if (stage instanceof EventStage) {
            // 물음표 아이콘
            g2d.setColor(new Color(0, 0, 150));
            g2d.drawString("?", symbolX, symbolY + symbolSize/2);
        } else if (stage instanceof GhostStage) {
            // 해골 아이콘
            g2d.setColor(new Color(150, 0, 0));
            g2d.drawString("☠", symbolX, symbolY + symbolSize/2);
        } else if (stage instanceof BuffStage) {
            // 별 아이콘
            g2d.setColor(new Color(0, 150, 0));
            g2d.drawString("★", symbolX, symbolY + symbolSize/2);
        } else if (stage instanceof ForceMove) {
            // 화살표 아이콘
            ForceMove fm = (ForceMove) stage;
            g2d.setColor(new Color(150, 0, 150));
            if (fm.getForceStage() == GameMaster.GHOST_FORCE_MOVE) {
                g2d.drawString("⇝", symbolX, symbolY + symbolSize/2); // 유령 방향
            } else if (fm.getForceStage() > 0) {
                g2d.drawString("→", symbolX, symbolY + symbolSize/2); // 앞으로
            } else {
                g2d.drawString("←", symbolX, symbolY + symbolSize/2); // 뒤로
            }
        } else if (stage instanceof NormalStage) {
            if (index == 0) { // 시작 칸
                g2d.setColor(new Color(0, 100, 0));
                g2d.drawString("S", symbolX, symbolY + symbolSize/2);
            } else if (index == BOARD_SIZE - 1) { // 골인 칸
                g2d.setColor(new Color(0, 100, 0));
                g2d.drawString("G", symbolX, symbolY + symbolSize/2);
            }
        }
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