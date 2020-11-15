package codeSliceExtractor.processJimple.processTrees;

import codeSliceExtractor.processJimple.codeData.Instruction;
import codeSliceExtractor.processJimple.codeData.Var;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Whenever the program enters a body of an if it is pushed a context. This allows to process ifs simultaneously
 * independent from each other.
 * It is an exact copy of the vars.
 */
public class Context {
    private int id; //for debugging purposes
    private HashMap<String, Var> vars = new HashMap<String, Var>();
    private HashMap<String, Var> affectedVars = new HashMap<String, Var>();
    private ArrayList<Instruction> lastIfVarHist;
    private ArrayList<Instruction> exitInsts = new ArrayList<Instruction>();

    public Context(HashMap<String, Var> vars, int id, ArrayList<Instruction> lastIfVarHist) {
        for (String varID : vars.keySet()) {
            Var varAux = vars.get(varID);
            Var var;
            if (varAux.getValue() != null) {
                ArrayList<Object> valAux = (ArrayList<Object>) varAux.getValue();
                var = new Var(varID, valAux.clone(), varAux.getInstructions().get(0));
            }
            else {
                var = new Var(varID);
                var.addInstructions((ArrayList<Instruction>) varAux.getInstructions().clone());
            }
            this.vars.put(varID, var);
        }
        this.id = id;
        this.lastIfVarHist = lastIfVarHist;
    }

    /**
     *******    Getters and Setters   ******
     */
    public int getId() {
        return this.id;
    }

    public HashMap<String, Var> getAffectedVars () {
        return this.affectedVars;
    }

    public Var getVar(String varId) {
        return this.vars.get(varId);
    }

    public HashMap<String, Var> getVars() { return this.vars; }

    public ArrayList<Instruction> getLastIfVarHist() { return this.lastIfVarHist; }

    public ArrayList<Instruction> getExitInsts() { return this.exitInsts; }

    public void addExitInst(Instruction inst) { this.exitInsts.add(inst); }

    /**
     *******    Simple Var (No array) Methods   ******
     */
    public void newVar(String varId, Instruction instruction) {
        Var var = new Var(varId, instruction);
        Var varAffected = new Var(varId, instruction);
        this.vars.put(varId, var);
        this.affectedVars.put(varId, varAffected);
    }

    public void addInstructionVar(String varId, Instruction instruction) {
        Var aux_var = this.vars.get(varId);
        vars.remove(varId);
        if (aux_var == null) { aux_var = new Var(varId, instruction); }
        else { aux_var.addInstruction(instruction); }
        vars.put(varId, aux_var);
        if (this.affectedVars.keySet().contains(varId)) {
            Var varAux = this.affectedVars.get(varId);
            affectedVars.remove(varId);
            varAux.addInstruction(instruction);
            this.affectedVars.put(varId, varAux);
        }
        else {
            Var varAux = new Var(varId, instruction);
            this.affectedVars.put(varId, varAux);
        }
    }

    public void removeInstructionVar(String varId, int index) {
        Var auxVar = this.vars.get(varId);
        vars.remove(varId);
        auxVar.removeInstruction(index);
        vars.put(varId, auxVar);
        this.affectedVars.put(varId, auxVar);
    }

    public ArrayList<Instruction> getHistoryVar(String varId) {
        if (this.vars.containsKey(varId)) {
            return this.vars.get(varId).getInstructions();
        }
        else {
            return null;
        }
    }

    /**
     *******    Var Array Methods   ******
     ** The Arrays instructions are saved in the value attribute on Var **
     */
    public void newVarArray(String varId, ArrayList<Object> value, ArrayList<Instruction> arrayInst) {
        Object val = value.clone();
        Var var = new Var(varId, val, arrayInst);
        Var varAffected = new Var(varId, val, arrayInst);
        this.vars.put(varId, var);
        this.affectedVars.put(varId, varAffected);
    }

    public void updateIndexHistoryVar(String varID, int index, ArrayList<Instruction> instructions) {
        Var var = this.vars.get(varID);
        var.setHistoryIndex(index, instructions);
        this.vars.put(varID, var);
        var = this.vars.get(varID);
        var.setHistoryIndex(index, instructions);
        this.affectedVars.put(varID, var);
    }

    public void updateArrayHistoryVar (String varID, ArrayList<Instruction> instructions) {
        Var var = this.vars.get(varID);
        var.setArrayHistoryVar(instructions);
        this.vars.put(varID, var);
        this.affectedVars.put(varID, var);
    }

    public ArrayList<Instruction> getHistoryVarIndex (String varId, int index) {
        Var var = this.vars.get(varId);
        return var.getIndexHistory(index);
    }

    public ArrayList<Instruction> getHistoryArrayVar(String varId) {
        Var var = this.vars.get(varId);
        return var.getHistoryArray();
    }

    /**
     *******    Multidimensional Var Array Methods   ******
     */
    public void updateValueIndexVar (String varID, int index, ArrayList<Object> value) {
        Var var = this.vars.get(varID);
        var.setValueIndex(index, (ArrayList<Object>) value.clone());
        this.vars.put(varID, var);
        this.affectedVars.put(varID, var);
    }

    public void updateArrayValueVar (String varID, ArrayList<Object> value) {
        Var var = this.vars.get(varID);
        var.setArrayValueVar((ArrayList<Object>) value.clone());
        this.vars.put(varID, var);
        this.affectedVars.put(varID, var);
    }

    /** Is it multidimensional array **/
    public boolean isIndexArray (String varId) {
        Var var = this.vars.get(varId);
        return var.isArrayofArrays();
    }

    public Object getValueVar(String varId) {
        return this.vars.get(varId).getValue();
    }

    public ArrayList<Object> getValueVarIndex (String varId, int index) {
        Var var = this.vars.get(varId);
        return var.getIndexValue(index);
    }

    //add("ola");
    public void addValueHist(ArrayList<Object> value, String varId) {
        Var var = this.vars.get(varId);
        var.addValue(value);
        this.vars.put(varId, var);
    }

    //add(0, "ola");
    public void addValueHist(ArrayList<Object> value, String varId, int index) {
        Var var = this.vars.get(varId);
        var.addValue(value, index);
        this.vars.put(varId, var);
        this.affectedVars.put(varId, var);
    }

    public void clearHist(String varId) {
        Var var = this.vars.get(varId);
        var.clearValue();
        this.vars.put(varId, var);
        this.affectedVars.put(varId, var);
    }

    public int lastIndexArray(String varId) {
        return this.vars.get(varId).lastIndexOfArray();
    }

    public ArrayList<Instruction> removeHist(String varId, int index) {
        Var var = this.vars.get(varId);
        ArrayList<Instruction> removedValue = var.removeValue(index);
        this.vars.put(varId, var);
        this.affectedVars.put(varId, var);
        return removedValue;
    }

    public ArrayList<Instruction> setHist(String varId, int index, ArrayList<Object> value) {
        Var var = this.vars.get(varId);
        ArrayList<Instruction> oldValue = var.setValue(index, value);
        this.vars.put(varId, var);
        this.affectedVars.put(varId, var);
        return oldValue;
    }

    public void setValueVar(ArrayList<ArrayList<Object>> value, String varId) {
        Var var = this.vars.get(varId);
        var.setValue(value);
        this.affectedVars.put(varId, var);
        this.vars.put(varId, var);
    }
}
