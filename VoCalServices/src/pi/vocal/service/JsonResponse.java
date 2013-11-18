package pi.vocal.service;

public class JsonResponse<T> {

	private static final int JSON_TRUE = 1;
	private static final int JSON_FALSE = 0;
	
	private int success;
	private T content;
	
	public int getSuccess() {
		return success;
	}
	public void setSuccess(boolean isSuccessful) {
		// mapping from boolean to json
		this.success = isSuccessful ? JSON_TRUE : JSON_FALSE;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}
	
	
}
