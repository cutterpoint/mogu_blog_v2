package com.cutter.point.blog.xo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cutter.point.blog.base.entity.SuperEntity;

/**
 * <p>
 * 代办事项表
 * </p>
 *
 * @author xuzhixiang
 * @since 2019年6月29日10:26:30
 */
@TableName("t_todo")
public class Todo extends SuperEntity<Todo> {

    private static final long serialVersionUID = 1L;
    
    // 内容
    private String text;
    
    //管理员UID
    private String adminUid;
    
    // 表示事项是否完成
    private Boolean done;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAdminUid() {
		return adminUid;
	}

	public void setAdminUid(String adminUid) {
		this.adminUid = adminUid;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
