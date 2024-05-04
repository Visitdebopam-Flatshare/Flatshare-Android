package com.joinflatshare.pojo.faq;

import java.util.ArrayList;

public class FaqItem {
    private String question = "";
    private String subquestion = "";

    public String getSubquestion() {
        return subquestion;
    }

    public void setSubquestion(String subquestion) {
        this.subquestion = subquestion;
    }

    private ArrayList<String> answers = new ArrayList<>();

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }
}
