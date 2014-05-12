package gradetrackerpro.graphics.container;
import gradetrackerpro.course.Course;
import gradetrackerpro.course.GradeGrouping;
import gradetrackerpro.graphics.GroupCreationWidget;
import gradetrackerpro.graphics.buttons.AColorButton;
import gradetrackerpro.graphics.buttons.ButtonAdd;
import gradetrackerpro.graphics.text.Label;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
public class CourseContainer extends AScrollableGraphicsContainer{
	private Course course;
	private Label title;
	private Label grade;
	private AColorButton addButton;
	private GroupCreationWidget createGroup;
	private ArrayList<GroupContainer> groups;
	public CourseContainer(int x, int y, int width, int height, Color slideColor, Course course) {
		super(x, y, width, height, slideColor);
		this.course = course;
		this.title = new Label(super.getX(),super.getY(),super.getWidth()/2,super.getHeight()*3/24,this.course.getName(),Color.white);
		super.addComponent(this.title);
		this.grade = new Label(super.getWidth()/2,super.getY(),super.getWidth()/2,super.getHeight()*3/24,this.course.getPercent()+"%", Color.yellow);
		super.addComponent(this.grade);
		this.addButton = createNewAddButton("new-group");
		super.addComponent(addButton);
		this.groups = new ArrayList<GroupContainer>();
	}
	private ButtonAdd createNewAddButton(String message){
		int buttonY = super.realHeight;
		ButtonAdd button = new ButtonAdd(super.getX()+8,buttonY+super.getY(),40,40,message);
		button.addReceiver(this);
		return button;
	}
	private void relocateElements(){
		int y = super.getY() + super.getHeight()*3/24 + 8;
		for(GroupContainer group:this.groups){
			super.removeComponent(group);
			group.setLocation(group.getX(),y);
			super.addComponent(group);
			y += group.getHeight() + 8;
		}
		if(this.createGroup!=null){
			super.removeComponent(this.createGroup);
			this.createGroup.setLocation(this.createGroup.getX(),y);
			super.addComponent(this.createGroup);
			y += this.createGroup.getHeight() + 8;
		}
		else{
			super.removeComponent(this.addButton);
			this.addButton=this.createNewAddButton("new-group");
			super.addComponent(this.addButton);
		}
	}
	public void ping(String title, String[] data){
		if(title.equals("mouse-data")){
			int x = Integer.parseInt(data[0]);
			int y = Integer.parseInt(data[1]);
			int event = Integer.parseInt(data[2]);
			this.addButton.mouseAction(x,y,event);
			if(this.createGroup!=null)
				this.createGroup.ping(title, data);
			for(int n=this.groups.size()-1;n>=0;n--)
				this.groups.get(n).ping(title,data);
			super.ping(title,data);
		}
		else if(title.equals("key-data")){
			if(this.createGroup!=null)
				this.createGroup.ping(title, data);
			for(GroupContainer group:this.groups)
				group.ping(title, data);
			super.ping(title, data);
			super.pushData("update",null);
		}
		else if(title.equals("add-new-group")){
			super.removeComponent(this.addButton);
			this.createGroup = new GroupCreationWidget(super.getX()+8,this.addButton.getY(),super.getWidth()-24-super.slideWidth,40);
			this.createGroup.addReceiver(this);
			this.addReceiver(createGroup);
			super.addComponent(this.createGroup);
			super.removeComponent(this.addButton);
			super.ping("update", null);
		}
		else if(title.equals("remove-widget")){
			super.removeComponent(this.createGroup);
			this.createGroup=null;
			this.addButton = this.createNewAddButton("new-group");
			super.addComponent(this.addButton);
			super.ping("update",null);
		}
		else if(title.equals("create-group")){
			super.removeComponent(this.createGroup);
			
			int percent = Integer.parseInt(data[0]);
			GradeGrouping gradeGroup = new GradeGrouping(this.course);
			this.course.addGroup(gradeGroup);
			
			int y = createGroup.getY();
			this.createGroup = null;
			
			GroupContainer newGroup = new GroupContainer(super.getX()+8,y,super.getWidth()-24-super.slideWidth,120,new Color(255,255,255,200),percent,gradeGroup);
			super.addComponent(newGroup);
			
			newGroup.addReceiver(this);
			this.addReceiver(newGroup);
			this.groups.add(newGroup);
			
			this.addButton = this.createNewAddButton("new-group");
			super.addComponent(this.addButton);
			
			super.ping("update",null);
		}
		else if(title.equals("cancel-group")){
			GroupContainer group = null;
			int x = Integer.parseInt(data[0]);
			int y = Integer.parseInt(data[1]);
			int percent = Integer.parseInt(data[2]);
			for(GroupContainer g:this.groups){
				if(g.getX()==x&&g.getY()==y&&g.getPercent()==percent){
					group = g;
					break;
				}
			}
			this.course.removeGroup(group.getGroup());
			super.removeComponent(group);
			this.removeReceiver(group);
			this.groups.remove(group);
			this.relocateElements();
			this.ping("update-grade",null);	
			super.ping("update",null);
		}
		else if(title.equals("update-grade")){
			this.grade.setHardText(this.course.getPercent()+"%");
		}
		else
			super.ping(title,data);
	}
	public void render(Graphics g){
		super.render(g);
		g.setColor(Color.white);
		g.drawRoundRect(super.getX(), super.getY(), super.getWidth(), super.getHeight(), 25, 25);
	}
}