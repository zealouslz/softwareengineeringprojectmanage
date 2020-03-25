package com.zeal.softwareengineeringprojectmanage.bean;

public class ChoosedTopic {
    private Integer topicId;
    private  Integer count;

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ChoosedTopic{" +
                "topicId=" + topicId +
                ", count=" + count +
                '}';
    }
}
