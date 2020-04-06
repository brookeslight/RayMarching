package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Main extends Canvas implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7604959185792845431L;
	//engine
	private boolean running;
	private Thread thread;
	private int mpX, mpY;
	private ArrayList<Bound> bounds;
	private ArrayList<Circle> circles;
	private float x, y;
	private int mouseX, mouseY;
	private boolean collision = false;
	private static final int range = 300;
	private static final int itterations = 25;
	
	public static void main(String[] args) {
		new Main().start();
	}
	
	public synchronized void start() {
		if(this.running == true) {
			return;
		}
		this.thread = new Thread(this);
		this.thread.start();
		this.running = true;
	}
	
	public synchronized void stop() {
		this.running = false;
		//clean up
	}
	
	private void init() {
		JFrame frame = new JFrame();
		frame.setSize(960, 800);
		frame.setLayout(null);
		//add bar
		JPanel bar = new JPanel();
		bar.setLayout(null);
		bar.setBounds(0, 0, frame.getWidth(), 24);
		bar.setBackground(Color.darkGray);
		bar.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		bar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mpX = e.getX();
				mpY = e.getY();
			}
		});
		bar.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(frame.getLocation().x + e.getX() - mpX, frame.getLocation().y + e.getY() - mpY);
			}
		});
		JLabel title = new JLabel();
		title.setForeground(Color.black);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setVerticalAlignment(SwingConstants.CENTER);
		title.setText("Template");
		title.setBounds(0, 0, 72, 24);
		JButton close = new JButton();
		close.setBounds(bar.getWidth()-42, 0, 42, 24);
		close.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		close.setOpaque(false);
		close.setContentAreaFilled(false);
		close.setFocusPainted(false);
		close.setForeground(Color.black);
		close.setHorizontalAlignment(SwingConstants.CENTER);
		close.setVerticalAlignment(SwingConstants.CENTER);
		close.setText("X");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		JButton max = new JButton();
		max.setBounds(bar.getWidth()-84, 0, 42, 24);
		max.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		max.setOpaque(false);
		max.setContentAreaFilled(false);
		max.setFocusPainted(false);
		max.setForeground(Color.black);
		max.setHorizontalAlignment(SwingConstants.CENTER);
		max.setVerticalAlignment(SwingConstants.CENTER);
		max.setText("+");
		max.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
					frame.setExtendedState(JFrame.NORMAL);
					bar.setBounds(0, 0, frame.getWidth(), 24);
					close.setBounds(bar.getWidth()-42, 0, 42, 24);
					max.setBounds(bar.getWidth()-84, 0, 42, 24);
					setBounds(0, 0 + bar.getHeight(), frame.getWidth(), frame.getHeight() - bar.getHeight());
					//
					max.setText("+");
				} else {
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					bar.setBounds(0, 0, frame.getWidth(), 24);
					close.setBounds(bar.getWidth()-42, 0, 42, 24);
					max.setBounds(bar.getWidth()-84, 0, 42, 24);
					setBounds(0, 0 + bar.getHeight(), frame.getWidth(), frame.getHeight() - bar.getHeight());
					//
					max.setText("~");
				}
			}
		});
		bar.add(title);
		bar.add(close);
		bar.add(max);
		frame.add(bar);
		//add canvas
		this.setBounds(0, 0 + bar.getHeight(), frame.getWidth(), frame.getHeight() - bar.getHeight());
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
		frame.add(this);
		//create window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		this.requestFocus();
		//
		this.bounds = new ArrayList<Bound>();
		for(int i = 0; i < 5; i++) {
			this.bounds.add(new Circle(ThreadLocalRandom.current().nextInt(0, this.getWidth() + 1), ThreadLocalRandom.current().nextInt(0, this.getHeight() + 1), ThreadLocalRandom.current().nextInt(4, 128 + 1)));
		}
		for(int i = 0; i < 5; i++) {
			this.bounds.add(new Rectangle(ThreadLocalRandom.current().nextInt(0, this.getWidth() + 1), ThreadLocalRandom.current().nextInt(0, this.getHeight() + 1), ThreadLocalRandom.current().nextInt(4, 128 + 1), ThreadLocalRandom.current().nextInt(4, 128 + 1)));
		}
		this.bounds.add(new Triangle(200, 200, 300, 300, 0, 400));
		this.circles = new ArrayList<Circle>();
		this.x = this.getWidth()/2;
		this.y = this.getHeight()/2;
		this.circles.add(new Circle(this.x, this.y, (float) this.distanceToScene(this.x, this.y)));
	}
	
	@Override
	public void run() {
		this.init();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		while(this.running == true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				this.tick();
				updates++;
				delta--;
			}
			this.render();
			frames++;
					
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames + " TICKS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
		System.exit(0);
	}
	
	private void tick() {
		//reset list of circles
		this.circles.clear();
		//calculate direction unit vector
		float marchX = this.x;
		float marchY = this.y;
		//ray march
		float distanceTraveled = 0;
		for(int i = 0; i < itterations && distanceTraveled <= range; i++) {
			double dis = this.distanceToScene(marchX, marchY);
			this.circles.add(new Circle(marchX, marchY, (float)dis));
			if(dis < 1) {
				this.collision = true;
			} else {
				//calcualte unit dir vector
				float dirX = this.mouseX - this.x;
				float dirY = this.mouseY - this.y;
				double l = Math.hypot(dirX, dirY);
				dirX /= l;
				dirY /= l;
				//march
				marchX += dirX*dis;
				marchY += dirY*dis;
				distanceTraveled+=dis;
				this.collision = false;
			}
		}
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform af = g2d.getTransform();
		//start draw
			//bg
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
			//bounds
		for(Bound b: this.bounds) {
			b.render(g);
		}
			//circles
		g.setColor(Color.red);
		if(this.collision) {
			g.setColor(Color.green);
		}
		g.fillRect((int)this.x, (int)this.y, 1, 1);
		for(Circle c: this.circles) {
			c.render(g);
		}
		//end draw
		g2d.setTransform(af);
		g.dispose();
		bs.show();
	}
	
	private double distanceToScene(float x, float y) {
		double l = Double.MAX_VALUE;
		for(Bound b: this.bounds) {
			l = Math.min(b.distanceTo(x, y), l);
		}
		return l;
	}

}