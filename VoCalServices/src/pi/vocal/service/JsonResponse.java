package pi.vocal.service;

public class JsonResponse<T> {

	private int success;
	private T content;
	
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int errorCode) {
		this.success = errorCode;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}
	
	
}
