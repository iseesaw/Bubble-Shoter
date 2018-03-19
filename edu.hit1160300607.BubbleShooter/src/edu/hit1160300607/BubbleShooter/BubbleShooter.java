package edu.hit1160300607.BubbleShooter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;

public class BubbleShooter extends JPanel {

	int mx;
	int my;
	int mbX = 280;
	int mbY = 740;

	Image ball1 = new ImageIcon("1.png").getImage();
	Image ball2 = new ImageIcon("2.png").getImage();
	Image ball3 = new ImageIcon("3.png").getImage();
	Image ball4 = new ImageIcon("4.png").getImage();
	Image ball5 = new ImageIcon("5.png").getImage();
	Image ball6 = new ImageIcon("6.png").getImage();
	Image ball7 = new ImageIcon("7.png").getImage();
	Image ball8 = new ImageIcon("8.png").getImage();
	Image[] images = { ball1, ball2, ball3, ball4, ball5, ball6, ball7, ball8 };

	ArrayList<Ball> balls = new ArrayList<Ball>(); // 储存所有空中小球

	int index = 5;
	int lastIndex;
	Ball shootBall = new Ball(images[index], mbX, mbY);
	int taughX;
	int taughY;

	int score = 0; // 分数

	long time = 0; // 时间

	public BubbleShooter() {

		balls(); // 调用小球赋值方法，给所有小球赋值

		addMouseMotionListener(new MouseAdapter() { // 监视炮台的变化
			public void mouseMoved(MouseEvent e) {
				mx = e.getX();
				my = e.getY();
				repaint();
			}

		});

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				double a = mx - 305;
				double b = my - 765;
				double degree = -Math.atan(a / b);

				if (degree > 1.4) // 发射保护
					degree = 1.4;
				if (degree < -1.4)
					degree = -1.4;

				shootBall.panel = BubbleShooter.this;

				shootBall.degree = degree;

				if (!isOver()) {
					try {
						shootBall.start();
					} catch (IllegalThreadStateException e1) {
						JOptionPane.showMessageDialog(null, "Don't click before last ball is stoped");
					}

					repaint();
				}

				else {
					JOptionPane.showMessageDialog(null, "Game Over!");
					System.exit(0);
				}

			}
		});

	}

	@SuppressWarnings("deprecation")
	public void paint(Graphics g) { // 画面板
		super.paint(g);

		Image image = new ImageIcon("ground.jpg").getImage(); // 背景图
		g.drawImage(image, 0, 0, 580, 840, null);

		Image groundR = new ImageIcon("groundR.png").getImage();
		g.drawImage(groundR, 580, 0, 230, 840, null);

		Image gunG = new ImageIcon("gunG.png").getImage(); // 发射炮台
		// *************************************

		if (balls.size() < 35) {
			addBalls();
		}

		for (int i = 0; i < balls.size(); i++) {
			g.drawImage(balls.get(i).image, balls.get(i).getX(), balls.get(i).getY(), 50, 50, null);
		}

		g.drawImage(gunG, 155, 700, 300, 100, null); // 炮台

		g.drawImage(shootBall.getImage(), mbX, mbY, 50, 50, null); // 运动的小球

		boolean taugh = isTaugh();

		if (taugh) {
			shootBall.stop();
			taughX = mbX;
			taughY = mbY;

			Ball theBall = new Ball(images[index], taughX, taughY, index);

			balls.add(theBall); // 接触的小球添加到小球数组

			// 消去小球
			calculateScore(clearBalls(theBall) + removeBalls(theBall));

			mbX = 280;
			mbY = 740;
			index = (int) (Math.random() * 8); // 产生下一个发射小球
			shootBall = new Ball(images[index]);

			taugh = false;

		}

		// 得分
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		g.drawString("" + score, 680, 385);

		// 时间

		long nowTime = Math.round((System.currentTimeMillis() - time) / 1000); // 当前所用秒数
		long minute = nowTime / 60;
		long second = nowTime - minute * 60;
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		g.drawString(minute + ":" + second, 670, 220);

		// ***************************************
		// 画炮台
		double a = mx - 305;
		double b = my - 765;
		double degreeR = -Math.atan(a / b);

		Graphics2D g2 = (Graphics2D) g;
		g2.rotate(degreeR, 305, 765);
		Image gunS = new ImageIcon("gunS.png").getImage();
		g.drawImage(gunS, 255, 595, this);

	}

	public void balls() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 11; j++) {
				int dex = (int) (Math.random() * 8);
				if (i % 2 == 0) {

					balls.add(new Ball(images[dex], 50 * j + 10, 50 * i, dex));
				} else {
					balls.add(new Ball(images[dex], 50 * j + 10, 50 * i, dex));
				}
			}
		}
	}

	public void addBalls() {

		for (int i = 0; i < balls.size(); i++) { // 所有小球下降一层
			int theY = balls.get(i).getY();
			balls.get(i).setY(theY + 100);
		}

		// 最上层产生新的小球，开始添加小球，没有带dex，导致出错
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 11; j++) {

				int dex = (int) (Math.random() * 8);
				if (i % 2 == 0) {

					balls.add(new Ball(images[dex], 50 * j + 10, 50 * i, dex));
				} else {
					balls.add(new Ball(images[dex], 50 * j + 10, 50 * i, dex));
				}
			}
		}

	}

	@SuppressWarnings("deprecation")
	public boolean isTaugh() { // 特定角度的小球停靠位置不太理想，碰墙后斜射小球存在bug

		for (int j = 0; j < balls.size(); j++) {

			int distance = (int) (Math.sqrt(Math.pow(mbX - balls.get(j).x, 2) + Math.pow(mbY - balls.get(j).y, 2)));

			if (mbX != balls.get(j).x && mbY != balls.get(j).y) {

				double theDe = Math.atan(Math.abs(mbX - balls.get(j).x) / Math.abs(mbY - balls.get(j).y));

				if (distance < 50 && theDe < 0.74) { // 碰撞接触优化，设置特定角度，

					if (mbX < balls.get(j).x) // 碰撞间距调小，需要微调停靠位置
						mbX = mbX - 5;
					if (mbX > balls.get(j).x)
						mbX = mbX + 5;
					if (mbY < balls.get(j).y)
						mbY = mbY - 5;
					if (mbY > balls.get(j).y)
						mbY = mbY + 5;

					return true;
				}
			}

			if (Math.abs(mbX - balls.get(j).x) < 50 && distance < 50) {
				return true;
			}

		}

		return false;
	}

	// 消除小球

	public int clearBalls(Ball theBall) {
		ArrayList<Ball> sameBalls = new ArrayList<Ball>();

		for (int m = 0; m < balls.size(); m++) {
			if (balls.get(m) == theBall)
				balls.get(m).setIsSame(true);
			else
				balls.get(m).setIsSame(false);
		}

		for (int times = 0; times < 3; times++) // 增加检索次数
			for (int i = 0; i < balls.size(); i++) {
				if (balls.get(i).getIsSame()) // 检索在周围的并且是相同颜色的小球周围
					checkSame(balls.get(i));
			}

		int n = 0;
		for (int i = 0; i < balls.size(); i++) { // 相同颜色在一起的小球放一起
			if (balls.get(i).getIsSame()) {
				sameBalls.add(balls.get(i));
				n++;
			}
		}

		if (sameBalls.size() >= 3) // 个数大于三个则相消
		{
			balls.removeAll(sameBalls);
			return n;
		} else
			return 0;

	}

	public void checkSame(Ball theBall) { // 检查周围六个球

		for (int i = 0; i < balls.size(); i++) { // 遍历所有小球 如果颜色相同并且还没有标记的，就求距离，距离小于70 的就标记
			if (!balls.get(i).getIsSame() && theBall.getImageIndex() == balls.get(i).getImageIndex()) {
				int distance = (int) (Math
						.sqrt(Math.pow(theBall.x - balls.get(i).x, 2) + Math.pow(theBall.y - balls.get(i).y, 2)));
				if (distance < 70) {
					balls.get(i).setIsSame(true);
				}
			}
		}

	}

	public int removeBalls(Ball theBall) { // 试玩10分钟出现一个bug：将发射的小球当作未连接消除了
		ArrayList<Ball> notLinkedBalls = new ArrayList<Ball>();

		for (int k = 0; k < balls.size(); k++) {
			if (balls.get(k).y > 25)
				balls.get(k).setIsLinked(false);
			else
				balls.get(k).setIsLinked(true); // 将顶层小球标记为连接
		}

		for (int times = 0; times < 3; times++)
			for (int i = 0; i < balls.size(); i++) { // 以顶层小球为基础遍历所有小球，进行连接检查
				if (balls.get(i).getIsLinked())
					checkLinked(balls.get(i));
			}

		int num = 0;
		for (int j = 0; j < balls.size(); j++) {
			if (!balls.get(j).getIsLinked()) {
				notLinkedBalls.add(balls.get(j)); // 将标记为没连接的小球，放到一组
				num++;
			}
		}

		if (notLinkedBalls.size() == 1 && notLinkedBalls.get(0) == theBall) // 如果未连接的小球只有一个，并且等于发射的小球，返回0，并且不消除
			return 0;
		else {
			balls.removeAll(notLinkedBalls); // 没连接的小球一起消除，避免出现没有及时消除的bug
			return num;
		}

	}

	public void checkLinked(Ball theBall) {
		for (int i = 0; i < balls.size(); i++) {
			int distance = (int) (Math
					.sqrt(Math.pow(theBall.x - balls.get(i).x, 2) + Math.pow(theBall.y - balls.get(i).y, 2)));

			if (distance < 60) {
				balls.get(i).setIsLinked(true);
			}

		}

	}

	public void calculateScore(int number) { // 3-10; 4-20; 5-30; 6-40; 7-50
		score += number * (number - 2) * 10;

	}

	// 判断结束条件
	public boolean isOver() { // 发射点 （280,740）

		int count = 0;
		for (int k = 0; k < balls.size(); k++) {
			double d = Math.sqrt(Math.pow(balls.get(k).x - 280, 2) + Math.pow(balls.get(k).y - 740, 2));
			if (d < 80.0 && Math.abs(balls.get(k).x - 280) < 60.0)
				return true;
			if (d < 80.0)
				count++;

		}

		if (count >= 3)
			return true;
		else
			return false;
	}

	public static void main(String[] args) {

		BubbleShooter p = new BubbleShooter();
		p.time = System.currentTimeMillis();

		JFrame frame = new JFrame();

		frame.add(p);
		frame.setSize(810, 840);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}

// 小球类，继承线程
class Ball extends Thread {
	double degree;
	final int v = 10;
	BubbleShooter panel;
	Image image;
	int x;
	int y;
	int imageIndex;
	boolean isSame = false;
	boolean isLinked = false;

	public Ball() {

	}

	public Ball(Image image) {
		this.image = image;
	}

	public Ball(Image image, int x, int y) {
		this.image = image;
		this.x = x;
		this.y = y;
	}

	public Ball(Image image, int x, int y, int imageIndex) {
		this.image = image;
		this.x = x;
		this.y = y;
		this.imageIndex = imageIndex;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setImageIndex(int imageIndex) {
		this.imageIndex = imageIndex;
	}

	public int getImageIndex() {
		return imageIndex;
	}

	public void setIsLinked(boolean isLinked) {
		this.isLinked = isLinked;
	}

	public boolean getIsLinked() {
		return isLinked;
	}

	public void setIsSame(boolean isSame) {
		this.isSame = isSame;
	}

	public boolean getIsSame() {
		return isSame;
	}

	public String toString() {
		return "imageIndex:" + imageIndex + " x:" + x + " y:" + y;
	}

	public void run() {
		super.run();
		int vx = (int) (v * Math.cos(degree - Math.PI / 2));
		int vy = (int) (v * Math.sin(degree - Math.PI / 2));
		while (true) {
			panel.mbX += vx;
			panel.mbY += vy;
			if (panel.mbX <= 0 || panel.mbX > 530) { // 碰撞速度的改变
				vx = -vx;
			}

			if (panel.mbY <= 0 || panel.mbY > 800) {
				vy = -vy;
			}

			try {
				sleep(10);
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}
			panel.repaint();
		}

	}

}
