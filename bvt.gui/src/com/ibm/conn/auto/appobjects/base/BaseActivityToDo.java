package com.ibm.conn.auto.appobjects.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

public class BaseActivityToDo extends BaseActivityEntry implements BaseStateObject {

	private User assignTo;
	private boolean dueDateRandom = false;
	private Calendar dueDate;
	private boolean useCalPick;
	private Activity parent;
	private List<User> multipleAssignTo = new ArrayList<User>();

	public static abstract class Builder<T extends Builder<T>> extends BaseActivityEntry.Builder<T> {
		private User assignTo;
		private List<User> multipleAssignTo = new ArrayList<User>();		
		private boolean dueDateRandom = false;
		private Calendar dueData;
		private boolean useCalPick = false;
		private Activity parent;
		
		public Builder(String title) {
			super(title);
		}
		
		public T assignTo(User person) {
			this.assignTo = person;
			return self();
		}
		
		public T multipleAssignTo(User person){
			this.multipleAssignTo.add(person);
			return self();
		}
		
		public T multipleAssignTo(List<User> persons){
			this.multipleAssignTo.addAll(persons);
			return self();
		}
		
		public T dueDate(Calendar date) {
			this.dueData = date;
			return self();
		}
		
		public T dueDateRandom() {
			this.dueDateRandom = true;
			return self();
		}
		
		public T useCalPick(boolean value){
			this.useCalPick = value;
			return self();
		}
		
		public BaseActivityToDo build() {
			return new BaseActivityToDo(this);
		}
	}
	
	private static class Builder2 extends Builder<Builder2> {
        public Builder2(String title) {
			super(title);
		}

		@Override
        protected Builder2 self() {
            return this;
        }
    }

    public static Builder<?> builder(String title) {
        return new Builder2(title);
    }
	
	private BaseActivityToDo(Builder<?> b) {
		super(b);
		this.setAssignTo(b.assignTo);
		this.setMultipleAssignTo(b.multipleAssignTo);
		this.setDueDate(b.dueData);
		this.setDueDateRandom(b.dueDateRandom);
		this.setUseCalPick(b.useCalPick);
		this.setParent(b.parent);
	}

	public User getAssignTo() {
		return assignTo;
	}

	public List<User> getMultipleAssignTo() {
		return multipleAssignTo;
	}
	
	public void setAssignTo(User assignTo) {
		this.assignTo = assignTo;
	}

	public void setMultipleAssignTo(List<User> persons){
		this.multipleAssignTo = persons;
	}
	
	public Calendar getDueDate() {
		return dueDate;
	}

	public void setDueDate(Calendar dueData) {
		this.dueDate = dueData;
	}

	public boolean getDueDateRandom() {
		return dueDateRandom;
	}

	public void setDueDateRandom(boolean dueDateRandom) {
		this.dueDateRandom = dueDateRandom;
	}
	public void setParent(Activity parent){
		
		this.parent = parent;
		
	}
	public Activity getParent(){
		
		return parent;
		
	}
	
	public boolean getUseCalPick() {
		return useCalPick;
	}

	public void setUseCalPick(boolean useCalPick) {
		this.useCalPick = useCalPick;
	}
	
	public void create(ActivitiesUI ui) {
		ui.createToDo(this);
	}
	
	public Todo createTodoAPI(APIActivitiesHandler activitiesAPI){
		
		Todo newTodo = activitiesAPI.createActivityTodo(this.getTitle(), this.getDescription(), this.getTags(), this.getParent(), getMarkPrivate());
		return newTodo;
	}
	
}
