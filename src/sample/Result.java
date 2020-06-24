package sample;

public class Result {
    private String nickname;
    private Integer count;

    public Result(String nickname, Integer count){
        this.nickname = nickname;
        this.count = count;
    }



    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNickname() {
        return nickname;
    }
    public Integer getCount() {
        return count;
    }

}
