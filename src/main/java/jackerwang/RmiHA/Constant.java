package jackerwang.RmiHA;

public interface Constant {
	Integer CONNECTION_TIMEOUT=5000;
    String REGISTRYZ_PATH="/registry";
    String PROVIDER_PATH=REGISTRYZ_PATH+"/provider";
    String ZooHost="192.168.102.3";
    Integer ZooPort=2181;
}
