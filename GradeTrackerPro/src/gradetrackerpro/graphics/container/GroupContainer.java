package gradetrackerpro.graphics.container;
import gradetrackerpro.course.Grade;
import gradetrackerpro.course.GradeGrouping;
import gradetrackerpro.graphics.GradeCreationWidget;
import gradetrackerpro.graphics.GradeDisplay;
import gradetrackerpro.graphics.buttons.AColorButton;
import gradetrackerpro.graphics.buttons.ASizeChangingButton;
import gradetrackerpro.graphics.buttons.ButtonAdd;
import gradetrackerpro.graphics.buttons.ButtonCancel;
import gradetrackerpro.graphics.text.Label;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
public class GroupContainer extends AScrollableGraphicsContainer {
	private int percent;
	private Label title;
	private ASizeChangingButton cancelButton;
	private AColorButton addButton;
	private GradeCreationWidget createGrade;
	private GradeGrouping group;
	private ArrayList<GradeDisplay> gradeDisplays;
	public GroupContainer(int x, int y, int width, int height, Color slideColor, int percent, GradeGrouping group) {
		super(x,y,width,height,slideColor);
		this.group=group;
		this.group.setPercent(percent);
		this.gradeDisplays = new ArrayList<GradeDisplay>();
		this.percent=percent;
		this.title = new Label(super.getX()+8,super.getY()+8,super.getWidth()/2,super.getHeight()*3/24,this.percent+"%",Color.white);
		super.addComponent(this.title);
		this.cancelButton = new ButtonCancel(super.getX()+super.getWidth()-8-super.getHeight()*4/24,super.getY()+8,super.getHeight()*4/24,super.getHeight()*4/24,super.getHeight()/48);
		this.cancelButton.addReceiver(this);
		super.addComponent(this.cancelButton);
		this.addButton = this.createNewAddButton("new-grade");
		super.addComponent(this.addButton);
	}
	public GradeGrouping getGroup(){
		return this.group;
	}
	private ButtonAdd createNewAddButton(String message){
		int buttonY = super.realHeight;
		ButtonAdd button = new ButtonAdd(super.getX()+8,buttonY+super.getY(),32,32,message);
		button.addReceiver(this);
		return button;
	}
	private void addGradeDisplay(Grade grade){
		int y = super.realHeight;
		GradeDisplay display = new GradeDisplay(super.getX()+8,y+super.getY(),super.getWidth()-16-16,32, grade);
		super.addComponent(display);
		display.addReceiver(this);
		this.addReceiver(display);
		this.gradeDisplays.add(display);
	}
	public void ping(String title, String[] data){
		if(title.equals("mouse-data")){
			int x = Integer.parseInt(data[0]);
			int y = Integer.parseInt(data[1]);
			int event = Integer.parseInt(data[2]);
			this.cancelButton.mouseAction(x,y,event);
			this.addButton.mouseAction(x,y,event);
			if(this.createGrade!=null)
				this.createGrade.ping(title,data);
		}
		else if(title.equals("key-data")){
			if(this.createGrade!=null)
				this.createGrade.ping(title,data);
		}
		else if(title.equals("cancel")){
			String[] pushData = {super.getX()+"",super.getY()+"",this.percent+""};
			super.pushData("cancel-group", pushData);
		}
		else if(title.equals("add-new-grade")){
			super.removeComponent(this.addButton);
			this.createGrade = new GradeCreationWidget(super.getX()+8,this.addButton.getY(),super.getWidth()-24-super.slideWidth,40);
			this.createGrade.addReceiver(this);
			this.addReceiver(createGrade);
			super.addComponent(this.createGrade);
			super.removeComponent(this.addButton);
			super.ping("update", null);
		}
		else if(title.equals("remove-widget")){
			if(this.createGrade==null)
				return;
			super.removeComponent(this.createGrade);
			this.createGrade=null;
			this.addButton = this.createNewAddButton("new-grade");
			super.addComponent(this.addButton);
			super.ping("update",null);
		}
		else if(title.equals("create-grade")){
			super.removeComponent(this.createGrade);
			this.createGrade = null;
			String name = data[0];
			int actual = Integer.parseInt(data[1]);
			int total = Integer.parseInt(data[2]);
			Grade grade = new Grade(name,actual,total);
			this.group.addGrade(grade);
			this.updateTotalGrade();
			this.addGradeDisplay(grade);
		}
	}
	private void updateTotalGrade(){
		double contribution = this.group.getValue();
		String[] data = {contribution+""};
		super.pushData("update-grade", data);
	}
	public int getPercent(){
		return this.percent;
	}
	public void render(Graphics g){
		super.render(g);
		g.setColor(Color.white);
		g.drawRoundRect(super.getX(), super.getY(), super.getWidth(), super.getHeight(), 25, 25);
	}
}