package codeSliceExtractor.processJimple.codeData;

import java.util.ArrayList;

public class Var {
    private String id;
    private Object value;
    private ArrayList<Instruction> instructions = new ArrayList<Instruction>();

    /**
     *******    Constructors   ******
     */
    public Var(String id, Instruction instruction) {
        this.id = id;
        this.instructions.add(instruction);
    }

    public Var(String id, Object value, Instruction instruction) {
        this.id = id;
        this.value = value;
        this.instructions.add(instruction);
    }

    public Var(String id, Object value, ArrayList<Instruction> instructions) {
        this.id = id;
        this.value = value;
        this.instructions = instructions;
    }

    public Var(String id) {
        this.id = id;
    }

    /**
     *******    Getters and Setters   ******
     */
    public String getId() { return this.id; }

    public ArrayList<Instruction> getInstructions() { return this.instructions; }

    public Object getValue() { return this.value; }

    public void setInstructions(ArrayList<Instruction> instructions) { this.instructions = instructions; }

    public void setValue(ArrayList<ArrayList<Object>> value) {this.value = value; }

    public ArrayList<Instruction> setValue(int index, ArrayList<Object> newValue) {
        ArrayList<ArrayList<Object>> val = (ArrayList<ArrayList<Object>>) this.value;
        ArrayList<Instruction> oldHist = new ArrayList<Instruction>();
        if(index < val.size()) {
            ArrayList<Object> oldValue = val.set(index, newValue);
            boolean array = false;
            for(Object obj : oldValue) {
                if(obj instanceof Instruction) {
                    Instruction instruction = (Instruction) obj;
                    oldHist.add(instruction);
                }
                else { array = true; }
            }
            if(array) { oldHist.add(new Instruction(Tag.ARRAY)); }
            this.value = val;
        }
        return oldHist;
    }

    public void setId(String id) { this.id = id;}

    /**
     *******    Instructions methods   ******
     */
    public void addInstruction(Instruction instruction) { this.instructions.add(instruction); }

    public void addInstructions(ArrayList<Instruction> instructions) { this.instructions.addAll(instructions); }

    public void removeInstruction(int index) {
        if(index < this.instructions.size()) { this.instructions.remove(index); }
    }

    /**
     *******    Value methods   ******
     */
    public void addValue(ArrayList<Object> newValue) {
        if (this.value == null) { this.value = new ArrayList<ArrayList<Object>>(); }
        ArrayList<ArrayList<Object>> val = (ArrayList<ArrayList<Object>>) this.value;
        val.add(newValue);
        this.value = val;
    }

    public void addValue(ArrayList<Object> newValue, int index) {
        if (this.value == null) { this.value = new ArrayList<ArrayList<Object>>(); }
        ArrayList<ArrayList<Object>> val = (ArrayList<ArrayList<Object>>) this.value;
        if(index < val.size()) {
            val.add(index, newValue);
            this.value = val;
        }
    }

    public void addValueIndex(ArrayList<ArrayList<Object>> newValues) {
        ArrayList<ArrayList<Object>> value = (ArrayList<ArrayList<Object>>) this.value;
        if (value == null) { value = new ArrayList<ArrayList<Object>>(); }
        ArrayList<Object> subValue;
        if(newValues != null) {
            for (int i = 0; i < value.size(); i++) {
                subValue = (value.get(i) == null) ? new ArrayList<Object>() : (ArrayList<Object>) value.get(i).clone();
                if(i < newValues.size()) {
                    if(newValues.get(i) != null) {
                        int size = newValues.get(i).size();
                        for (int j = 0; j < size; j++) {
                            Object obj = newValues.get(i).get(j);
                            subValue.add(obj);
                        }
                        value.set(i, subValue);
                    }
                }
            }
        }
        this.value = value;
    }

    public void clearValue() {
        ArrayList<ArrayList<Object>> value = (ArrayList<ArrayList<Object>>) this.value;
        if (value == null) { value = new ArrayList<ArrayList<Object>>(); }
        value.clear();
        this.value = value;
    }

    public int lastIndexOfArray() {
        ArrayList<ArrayList<Object>> value = (ArrayList<ArrayList<Object>>) this.value;
        if(value == null) {
            this.value = new ArrayList<ArrayList<Object>>();
            return -1;
        }
        else {
            return value.size() - 1;
        }
    }

    public ArrayList<Instruction> removeValue(int index) {
        ArrayList<ArrayList<Object>> value = (ArrayList<ArrayList<Object>>) this.value;
        ArrayList<Instruction> removedHist = new ArrayList<Instruction>();
        if(value != null) {
            if(index < value.size()) {
                ArrayList<Object> removedValue = value.remove(index);
                boolean array = false;
                for(Object obj : removedValue) {
                    if(obj instanceof Instruction) {
                        Instruction instruction = (Instruction) obj;
                        removedHist.add(instruction);
                    }
                    else { array = true; }
                }
                if(array) { removedHist.add(new Instruction(Tag.ARRAY)); }
                this.value = value;
            }
        }
        return removedHist;
    }

    public void setHistoryIndex(int index, ArrayList<Instruction> instructions) {
        try {
            ArrayList<ArrayList<Instruction>> value = (ArrayList<ArrayList<Instruction>>) this.value;
            value.set(index, (ArrayList<Instruction>) instructions.clone());
        }
        catch (Exception e) { /** ...Nothing to do... **/ }
    }

    public void addInstructionsArrayIndex(int index, ArrayList<Instruction> instructions) {
        try {
            ArrayList<ArrayList<Instruction>> value = (ArrayList<ArrayList<Instruction>>) this.value;
            ArrayList<Instruction> insts = value.get(index);
            insts.addAll(instructions);
        }
        catch (Exception e) { /** ...Nothing to do... **/ }
    }

    public void setArrayHistoryVar(ArrayList<Instruction> instructions) {
        try {
            ArrayList<ArrayList<Instruction>> value = (ArrayList<ArrayList<Instruction>>) this.value;
            for (int i = 0; i < value.size(); i++) {
                ArrayList<Instruction> instructionsList = (ArrayList<Instruction>) value.get(i).clone();
                instructionsList.addAll(instructions);
                value.set(i, instructionsList);
            }
        }
        catch (Exception e) {  /** ...Nothing to do... **/ }
    }

    public void setValueIndex(int index, ArrayList<Object> value) {
        ArrayList<ArrayList<Object>> valueVar = (ArrayList<ArrayList<Object>>) this.value;
        if(index < valueVar.size()) {
            valueVar.set(index, (ArrayList<Object>) value.clone());
        }
    }

    public void setArrayValueVar(ArrayList<Object> value) {
        if (this.value == null) { this.value = new ArrayList<ArrayList<Object>>(); }
        ArrayList<ArrayList<Object>> valueVar = (ArrayList<ArrayList<Object>>) this.value;
        for (int i = 0; i < valueVar.size(); i++) {
            ArrayList<Object> valueList = (ArrayList<Object>) valueVar.get(i).clone();
            valueList.addAll(value);
            value.set(i, valueList);
        }
    }

    public ArrayList<Instruction> getIndexHistory (int index) {
        try {
            ArrayList<ArrayList<Instruction>> arrayInstructions = (ArrayList<ArrayList<Instruction>>) this.value;
            if(index < arrayInstructions.size()) {
                return arrayInstructions.get(index);
            }
            return new ArrayList<Instruction>();
        }
        catch (Exception e) { return new ArrayList<Instruction>(); }
    }

    public ArrayList<Instruction> getHistoryArray () {
        try {
            ArrayList<ArrayList<Instruction>> val = (ArrayList<ArrayList<Instruction>>) this.value;
            ArrayList<Instruction> allInstructions = new ArrayList<Instruction>();
            if(val != null) {
                for (ArrayList<Instruction> instructionsIndex : val) {
                    allInstructions.addAll(instructionsIndex);
                }
            }
            return allInstructions;
        }
        catch (Exception e) { return new ArrayList<Instruction>(); }
    }

    public ArrayList<Object> getIndexValue (int index) {
        ArrayList<ArrayList<Object>> objs = (ArrayList<ArrayList<Object>>) this.value;
        ArrayList<Object> objsIndex = (objs == null) ? new ArrayList<Object>() : (ArrayList<Object>) objs.get(index);
        return objsIndex;
    }

    public boolean isArray() {
        if(this.value != null) {
            if(this.value instanceof ArrayList<?>) {
                ArrayList<Object> objs = (ArrayList<Object>) this.value;
                if(!objs.isEmpty()) {
                    if(objs.get(0) instanceof ArrayList<?>) {
                        ArrayList<Object> objsIndex = (ArrayList<Object>) objs.get(0);
                        if(!objsIndex.isEmpty()) { return (objsIndex.get(0) instanceof Instruction); }
                    }
                }
            }
        }
        return false;
    }
    public boolean isArrayofArrays() {
        if(this.value != null) {
            if(this.value instanceof ArrayList<?>) {
                ArrayList<Object> objs = (ArrayList<Object>) this.value;
                if(!objs.isEmpty()) {
                    if(objs.get(0) instanceof ArrayList<?>) {
                        ArrayList<Object> objsIndex = (ArrayList<Object>) objs.get(0);
                        if(!objsIndex.isEmpty()) { return !(objsIndex.get(0) instanceof Instruction); }
                    }
                }
            }
        }
        return false;
    }

    /**
     *******    Auxilary methods   ******
     */
    public ArrayList getSourceHist() {
        ArrayList<Instruction> sourceInsts = new ArrayList<Instruction>();
        for(Instruction inst : this.instructions) {
            if(inst.getTag() == Tag.SOURCE) {
                sourceInsts.add(inst);
            }
        }
        return sourceInsts;
    }

    public void printInstructions() {
        for(Instruction instruction : this.instructions) {
            instruction.print();
        }
    }
}
