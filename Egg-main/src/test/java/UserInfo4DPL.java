import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import java.io.Serializable;

/**
 * Created by andrew on 15-1-28.
 */
@Entity
public class UserInfo4DPL implements Serializable{
    @PrimaryKey
    private String userId;
    private String userName;
    private String description;

    public UserInfo4DPL() {
    }

    public UserInfo4DPL(String userId, String userName, String description) {
        this.userId = userId;
        this.userName = userName;
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UserInfo4DPL{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
