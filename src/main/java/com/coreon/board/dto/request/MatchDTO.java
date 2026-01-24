package com.coreon.board.dto.request;

import java.io.Serializable;

public class MatchDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;                // member.id
    private String username;          // member.userName (또는 userName에 해당하는 값)
    private String age;
    private int score;
    private String imageUrl;
    private String home;
    private String gender;  
    public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	private String lastMessage;
    private String lastMessageTime;
    public int getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}
	private int unreadCount;

    public MatchDTO() {}

    // 필요하면 사용하는 생성자
    public MatchDTO(String id, String username, String age, int score, String imageUrl, String home) {
        this.id = id;
        this.username = username;   
        this.age = age;
        this.score = score;
        this.imageUrl = imageUrl;
        this.home = home;
    }

    // Getter / Setter
    public String getId() { 
        return id; 
    }
    public void setId(String id) {
        this.id = id;
    }

   
    public String getUsername() { 
        return username; 
    }
    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getAge() { 
        return age; 
    }
    public void setAge(String age) { 
        this.age = age; 
    }

    public int getScore() { 
        return score; 
    }
    public void setScore(int score) { 
        this.score = score; 
    }

    public String getHome() { 
        return home; 
    }
    public void setHome(String home) { 
        this.home = home; 
    }

    public String getImageUrl() { 
        return imageUrl; 
    }
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; 
    }

    public String getLastMessage() { 
        return lastMessage; 
    }
    public void setLastMessage(String lastMessage) { 
        this.lastMessage = lastMessage; 
    }

    public String getLastMessageTime() { 
        return lastMessageTime; 
    }
    public void setLastMessageTime(String lastMessageTime) { 
        this.lastMessageTime = lastMessageTime; 
    }
}
