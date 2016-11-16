package com.florianwoelki.info5pk.neuronalnetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Florian Woelki on 16.11.16.
 */
public class NeuronalNetwork {

    private List<InputNeuron> inputNeurons = new ArrayList<>();
    private List<WorkingNeuron> hiddenNeurons = new ArrayList<>();
    private List<WorkingNeuron> outputNeurons = new ArrayList<>();

    public void addInputNeuron(InputNeuron inputNeuron) {
        inputNeurons.add( inputNeuron );
    }

    public void addHiddenNeuron(WorkingNeuron workingNeuron) {
        hiddenNeurons.add( workingNeuron );
    }

    public void addOutputNeuron(WorkingNeuron workingNeuron) {
        outputNeurons.add( workingNeuron );
    }

    public void generateHiddenNeurons(int amount) {
        for ( int i = 0; i < amount; i++ ) {
            hiddenNeurons.add( new WorkingNeuron() );
        }
    }

    public void randomizeAllWeights() {
        for ( WorkingNeuron hiddenNeuron : hiddenNeurons ) {
            hiddenNeuron.randomizeWeights();
        }

        for ( WorkingNeuron outputNeuron : outputNeurons ) {
            outputNeuron.randomizeWeights();
        }
    }

    public void generateFullMesh() {
        for ( WorkingNeuron hiddenNeuron : hiddenNeurons ) {
            for ( InputNeuron inputNeuron : inputNeurons ) {
                hiddenNeuron.addNeuronConnection( inputNeuron, 1 );
            }
        }

        for ( WorkingNeuron outputNeuron : outputNeurons ) {
            for ( WorkingNeuron hiddenNeuron : hiddenNeurons ) {
                outputNeuron.addNeuronConnection( hiddenNeuron, 1 );
            }
        }
    }

    public WorkingNeuron getOutputNeuronFromIndex(int index) {
        return outputNeurons.get( index );
    }

    public NeuronalNetwork cloneFullMesh() {
        NeuronalNetwork copy = new NeuronalNetwork();

        for ( InputNeuron inputNeuron : inputNeurons ) {
            copy.addInputNeuron( (InputNeuron) inputNeuron.nameCopy() );
        }

        for ( WorkingNeuron hiddenNeuron : hiddenNeurons ) {
            copy.addHiddenNeuron( (WorkingNeuron) hiddenNeuron.nameCopy() );
        }

        for ( WorkingNeuron outputNeuron : outputNeurons ) {
            copy.addOutputNeuron( (WorkingNeuron) outputNeuron.nameCopy() );
        }

        copy.generateFullMesh();

        for ( int i = 0; i < hiddenNeurons.size(); i++ ) {
            List<Connection> connectionsOriginal = hiddenNeurons.get( i ).getConnections();
            List<Connection> connectionsCopy = copy.hiddenNeurons.get( i ).getConnections();
            if ( connectionsOriginal.size() != connectionsCopy.size() ) {
                return null;
            }

            for ( int k = 0; k < connectionsOriginal.size(); k++ ) {
                connectionsCopy.get( k ).weight = connectionsOriginal.get( k ).weight;
            }
        }

        for ( int i = 0; i < outputNeurons.size(); i++ ) {
            List<Connection> connectionsOriginal = outputNeurons.get( i ).getConnections();
            List<Connection> connectionsCopy = copy.outputNeurons.get( i ).getConnections();
            if ( connectionsOriginal.size() != connectionsCopy.size() ) {
                return null;
            }

            for ( int k = 0; k < connectionsOriginal.size(); k++ ) {
                connectionsCopy.get( k ).weight = connectionsOriginal.get( k ).weight;
            }
        }

        return copy;
    }

}