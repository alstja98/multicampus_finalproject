package com.probee.waggle.model.dto;

public class UserRatingDto {
	private int ur_UCode;
	private int ur_Rate;
	private String ur_Attr1;
	private String ur_Attr2;
	private String ur_Attr3;
	private String ur_Stat;
	
	public UserRatingDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserRatingDto(int ur_UCode, int ur_Rate, String ur_Attr1, String ur_Attr2, String ur_Attr3, String ur_Stat) {
		super();
		this.ur_UCode = ur_UCode;
		this.ur_Rate = ur_Rate;
		this.ur_Attr1 = ur_Attr1;
		this.ur_Attr2 = ur_Attr2;
		this.ur_Attr3 = ur_Attr3;
		this.ur_Stat = ur_Stat;
	}

	public int getUr_UCode() {
		return ur_UCode;
	}

	public void setUr_UCode(int ur_UCode) {
		this.ur_UCode = ur_UCode;
	}

	public int getUr_Rate() {
		return ur_Rate;
	}

	public void setUr_Rate(int ur_Rate) {
		this.ur_Rate = ur_Rate;
	}

	public String getUr_Attr1() {
		return ur_Attr1;
	}

	public void setUr_Attr1(String ur_Attr1) {
		this.ur_Attr1 = ur_Attr1;
	}

	public String getUr_Attr2() {
		return ur_Attr2;
	}

	public void setUr_Attr2(String ur_Attr2) {
		this.ur_Attr2 = ur_Attr2;
	}

	public String getUr_Attr3() {
		return ur_Attr3;
	}

	public void setUr_Attr3(String ur_Attr3) {
		this.ur_Attr3 = ur_Attr3;
	}

	public String getUr_Stat() {
		return ur_Stat;
	}

	public void setUr_Stat(String ur_Stat) {
		this.ur_Stat = ur_Stat;
	}

	@Override
	public String toString() {
		return "UserRatingDto [ur_UCode=" + ur_UCode + ", ur_Rate=" + ur_Rate + ", ur_Attr1=" + ur_Attr1 + ", ur_Attr2="
				+ ur_Attr2 + ", ur_Attr3=" + ur_Attr3 + ", ur_Stat=" + ur_Stat + "]";
	}

	
	
}
