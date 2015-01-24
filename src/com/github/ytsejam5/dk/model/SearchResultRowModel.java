package com.github.ytsejam5.dk.model;

import java.util.List;

import com.github.ytsejam5.dk.data.DonburiBean;

public class SearchResultRowModel {

	private DonburiBean data = null;
	private List<String> snippetTextList = null;
	private List<DonburiBean> similarDonburiList = null;
	private ScoreModel scoreModel = null;

	public DonburiBean getData() {
		return data;
	}

	public void setData(DonburiBean data) {
		this.data = data;
	}

	public List<String> getSnippetTextList() {
		return snippetTextList;
	}

	public void setSnippetTextList(List<String> snippetTextList) {
		this.snippetTextList = snippetTextList;
	}

	public List<DonburiBean> getSimilarDonburiList() {
		return similarDonburiList;
	}

	public void setSimilarDonburiList(List<DonburiBean> similarDonburiList) {
		this.similarDonburiList = similarDonburiList;
	}

	public ScoreModel getScoreModel() {
		return scoreModel;
	}

	public void setScoreModel(ScoreModel scoreModel) {
		this.scoreModel = scoreModel;
	}

	public static class ScoreModel {

		private int score = -1;
		private double confidence = -1;
		private double fitness = -1;

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public double getConfidence() {
			return confidence;
		}

		public void setConfidence(double confidence) {
			this.confidence = confidence;
		}

		public double getFitness() {
			return fitness;
		}

		public void setFitness(double fitness) {
			this.fitness = fitness;
		}
	}
}
