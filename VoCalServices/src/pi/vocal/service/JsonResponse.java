package pi.vocal.service;

public class JsonResponse<T> {

	private int sucess;
	private T content;
	
	public int getSucess() {
		return sucess;
	}
	public void setSucess(int errorCode) {
		this.sucess = errorCode;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}
	
	
}
