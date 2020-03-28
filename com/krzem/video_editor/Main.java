package com.krzem.video_editor;



import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.Exception;
import java.lang.Math;
import javax.swing.JFrame;



public class Main extends Constants{
	public static void main(String[] args){
		new Main(args);
	}



	public double FPS=1;
	public int MOUSE=0;
	public int MOUSE_COUNT=0;
	public int MOUSE_BUTTON=0;
	public Vector MOUSE_POS=new Vector(0,0);
	public int SCROLL_D=0;
	public Keyboard KEYBOARD;
	public Editor e;
	public JFrame frame;
	public Canvas canvas;
	private int _mouse;
	private int _mouseC;
	private int _mouseB;
	private MouseEvent _mouseM;
	private int _sc;
	private Runnable _ru;
	private boolean _break=false;



	public Main(String[] args){
		this.init();
		this.frame_init();
		this.run();
	}



	public void init(){
		this.KEYBOARD=new Keyboard(this);
		this.e=new Editor(this);
	}



	public void frame_init(){
		Main cls=this;
		this.frame=new JFrame("Video Editor");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setUndecorated(true);
		this.frame.setResizable(false);
		this.frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				cls._quit();
			}
		});
		SCREEN.setFullScreenWindow(this.frame);
		this.canvas=new Canvas(this);
		this.canvas.setSize(WINDOW_SIZE.width,WINDOW_SIZE.height);
		this.canvas.setPreferredSize(new Dimension(WINDOW_SIZE.width,WINDOW_SIZE.height));
		this.canvas.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				cls._mouse=1;
				cls._mouseC=e.getClickCount();
				cls._mouseB=e.getButton();
			}
			public void mouseReleased(MouseEvent e){
				cls._mouse=2;
				cls._mouseC=e.getClickCount();
				cls._mouseB=e.getButton();
			}
			public void mouseClicked(MouseEvent e){
				cls._mouse=3;
				cls._mouseC=e.getClickCount();
				cls._mouseB=e.getButton();
			}
		});
		this.canvas.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseMoved(MouseEvent e){
				cls._mouseM=e;
			}
			public void mouseDragged(MouseEvent e){
				cls._mouseM=e;
			}
		});
		this.canvas.addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e){
				if (e.getWheelRotation()<0){
					cls._sc=1;
				}
				else{
					cls._sc=-1;
				}
			}
		});
		this.canvas.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e){
				if (cls.KEYBOARD==null){
					return;
				}
				cls.KEYBOARD.down(e);
			}
			public void keyReleased(KeyEvent e){
				if (cls.KEYBOARD==null){
					return;
				}
				cls.KEYBOARD.up(e);
			}
			public void keyTyped(KeyEvent e){
				if (cls.KEYBOARD==null){
					return;
				}
				cls.KEYBOARD.press(e);
			}
		});
		this.frame.setContentPane(this.canvas);
		this.canvas.requestFocus();
	}



	public void run(){
		Main cls=this;
		this._ru=new Runnable(){
			@Override
			public void run(){
				while (cls._break==false){
					Long s=System.currentTimeMillis();
					try{
						cls._update_events();
						cls.update();
						cls.canvas.repaint();
					}
					catch (Exception e){
						e.printStackTrace();
					}
					Long d=System.currentTimeMillis()-s;
					if (d==0){
						d=1L;
					}
					if ((double)Math.floor(1/(double)d*1e8)/1e5>cls.MAX_FPS){
						try{
							Thread.sleep((long)(1/(double)cls.MAX_FPS*1e3)-d);
						}
						catch (InterruptedException e){}
					}
					cls.FPS=(double)Math.floor(1/(double)(System.currentTimeMillis()-s)*1e8)/1e5;
				}
			}
		};
		new Thread(this._ru).start();
	}



	public void update(){
		this.e.update();
		if (this.KEYBOARD.pressed(27)){
			this._quit();
		}
		this.KEYBOARD.update();
	}



	public void draw(Graphics2D g){
		this.e.draw(g);
		g.drawString(Double.toString(this.FPS),50,50);
	}



	private void _update_events(){
		this.MOUSE=this._mouse+0;
		this.MOUSE_COUNT=this._mouseC+0;
		this.MOUSE_BUTTON=this._mouseB+0;
		if (this._mouse!=1){
			this._mouse=0;
			this._mouseC=0;
			this._mouseB=0;
		}
		if (this._mouseM!=null){
			this.MOUSE_POS=new Vector(this._mouseM.getPoint().x,this._mouseM.getPoint().y);
			this._mouseM=null;
		}
		this.SCROLL_D=this._sc+0;
		this._sc=0;
	}



	private void _quit(){
		if (this._break==true){
			return;
		}
		this._break=true;
		this.frame.dispose();
		this.frame.dispatchEvent(new WindowEvent(this.frame,WindowEvent.WINDOW_CLOSING));
	}
}