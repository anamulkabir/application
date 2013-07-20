package app.ennoviabd.com.umis;

public class MdlAdmission {
	private String idn;
	private String name;
	private String fname;
	private String sex;
	private String dob,address,admissiondate;
	public String getIdn()
	{
		return idn;
	}
	public void setIdn(String idn)
	{
		this.idn=idn;
	}
	public String getName()
	{
		return name;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public void setName(String name)
	{
		this.name=name;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAdmissiondate() {
		return admissiondate;
	}
	public void setAdmissiondate(String admissiondate) {
		this.admissiondate = admissiondate;
	}
	
	

}
