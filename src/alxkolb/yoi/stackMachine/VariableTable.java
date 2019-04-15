package alxkolb.yoi.stackMachine;
import java.util.HashMap;

class VariableTable {
    private HashMap<String, String> types = new HashMap<>();
    private HashMap<String, Object> values = new HashMap<>();

    public void addVariable(String name, String type, Object value) throws Exception {
        if (!types.containsKey(name) && !values.containsKey(name)) {
            types.put(name, type);
            values.put(name, value);
        } else {
            if (types.get(name).equals(type)) {
                values.replace(name, value);
            } else
                throw new Exception("Переменная "+name+" уже объявлена с типом "+types.get(name));
        }
    }

    public Object getVariableValue(String variableName) throws Exception {
        if (values.containsKey(variableName)) {
            return values.get(variableName);
        } else
            throw new Exception("Переменная "+variableName+" не инициализированна");
    }

    public String getVariableType(String variableName) throws Exception {
        if (types.containsKey(variableName)) {
            return types.get(variableName);
        } else
            throw new Exception("Переменная "+variableName+" не инициализированна");
    }

    public boolean check(String variable) {
        return values.containsKey(variable);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
