/*
 *Copyright (c) 2015 Andrew-Wang.
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
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
