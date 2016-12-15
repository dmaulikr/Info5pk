package com.florianwoelki.info5pk.creature;

import com.florianwoelki.info5pk.level.Level;
import com.florianwoelki.info5pk.level.tile.Tile;
import com.florianwoelki.info5pk.math.MathUtil;
import com.florianwoelki.info5pk.neuralnetwork.NeuralNetwork;
import com.florianwoelki.info5pk.neuralnetwork.neuron.InputNeuron;
import com.florianwoelki.info5pk.neuralnetwork.neuron.WorkingNeuron;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Florian Woelki on 16.11.16.
 */
public abstract class Creature {

    public static int maximumGeneration = 1;
    public static int currentId;
    public static Creature oldestCreatureEver = new TestCreature( null, 0, 0, 0 );

    public java.util.List<Creature> children = new ArrayList<>();
    private long motherId;
    private long id;
    private java.util.List<Long> childIds = new ArrayList<>();
    protected Creature mother;
    protected int generation = 1;

    protected final float COST_EAT = 0.1f;
    protected final float GAIN_EAT = 1f;
    protected final float COST_PERMANENT = 0.01f;
    protected final float COST_WALK = 0.05f;
    protected final float COST_ROTATE = 0.05f;

    protected final float FOOD_DROP_PERCENTAGE = 0;

    protected final float MOVE_SPEED = 2f;

    protected final float START_ENERGY = 150;
    protected final float MINIMUM_SURVIVAL_ENERGY = 100;

    protected final int SIZE = 8;
    protected final int FEELER_SIZE = 4;

    protected float x;
    protected float y;
    protected float viewAngle;

    protected float feelerX;
    protected float feelerY;
    protected float feelerAngle;

    protected float energy = this.START_ENERGY;
    protected float age = 0;

    protected NeuralNetwork brain;

    protected final String NAME_IN_BIAS = "bias";
    protected final String NAME_IN_FOOD_VALUE_POSITION = "Food Value Position";
    protected final String NAME_IN_FOOD_VALUE_FEELER = "Food Value Feeler";
    protected final String NAME_IN_ENERGY = "Energy";
    protected final String NAME_IN_AGE = "Age";
    protected final String NAME_IN_WATER_ON_FEELER = "Water On Feeler";
    protected final String NAME_IN_WATER_ON_CREATURE = "Water On Creature";

    protected final String NAME_OUT_BIRTH = "Birth";
    protected final String NAME_OUT_ROTATE = "Rotate";
    protected final String NAME_OUT_FORWARD = "Forward";
    protected final String NAME_OUT_FEELER_ANGLE = "Feeler Angle";
    protected final String NAME_OUT_EAT = "Eat";

    protected InputNeuron inBias = new InputNeuron();
    protected InputNeuron inFoodValuePosition = new InputNeuron();
    protected InputNeuron inFoodValueFeeler = new InputNeuron();
    protected InputNeuron inEnergy = new InputNeuron();
    protected InputNeuron inAge = new InputNeuron();
    protected InputNeuron inWaterOnFeeler = new InputNeuron();
    protected InputNeuron inWaterOnCreature = new InputNeuron();

    protected WorkingNeuron outBirth = new WorkingNeuron();
    protected WorkingNeuron outRotate = new WorkingNeuron();
    protected WorkingNeuron outForward = new WorkingNeuron();
    protected WorkingNeuron outFeelerAngle = new WorkingNeuron();
    protected WorkingNeuron outEat = new WorkingNeuron();

    protected int amountOfMemory = 1;

    protected Level level;
    public float mouseWheelScale = 1;

    protected Color color;

    public Creature( Level level, float x, float y, float viewAngle ) {
        this.id = currentId++;

        this.level = level;
        this.x = x;
        this.y = y;
        this.viewAngle = viewAngle;

        this.inBias.setName( this.NAME_IN_BIAS );
        this.inFoodValuePosition.setName( this.NAME_IN_FOOD_VALUE_POSITION );
        this.inFoodValueFeeler.setName( this.NAME_IN_FOOD_VALUE_FEELER );
        this.inEnergy.setName( this.NAME_IN_ENERGY );
        this.inAge.setName( this.NAME_IN_AGE );
        this.inWaterOnFeeler.setName( this.NAME_IN_WATER_ON_FEELER );
        this.inWaterOnCreature.setName( this.NAME_IN_WATER_ON_CREATURE );

        this.outBirth.setName( this.NAME_OUT_BIRTH );
        this.outRotate.setName( this.NAME_OUT_ROTATE );
        this.outForward.setName( this.NAME_OUT_FORWARD );
        this.outFeelerAngle.setName( this.NAME_OUT_FEELER_ANGLE );
        this.outEat.setName( this.NAME_OUT_EAT );

        this.brain = new NeuralNetwork();

        this.brain.addInputNeuron( this.inBias );
        this.brain.addInputNeuron( this.inFoodValuePosition );
        this.brain.addInputNeuron( this.inFoodValueFeeler );
        this.brain.addInputNeuron( this.inEnergy );
        this.brain.addInputNeuron( this.inAge );
        this.brain.addInputNeuron( this.inWaterOnFeeler );
        this.brain.addInputNeuron( this.inWaterOnCreature );

        this.brain.generateHiddenNeurons( 10 );

        this.brain.addOutputNeuron( this.outBirth );
        this.brain.addOutputNeuron( this.outRotate );
        this.brain.addOutputNeuron( this.outForward );
        this.brain.addOutputNeuron( this.outFeelerAngle );
        this.brain.addOutputNeuron( this.outEat );

        this.brain.generateFullMesh();

        this.brain.randomizeAllWeights();
        this.calculateFeelerPosition();

        this.color = new Color( (float) MathUtil.random.nextDouble(), (float) MathUtil.random.nextDouble(), (float) MathUtil.random.nextDouble() );
    }

    public Creature( Level level, Creature mother ) {
        this.id = currentId++;

        this.level = level;
        this.mother = mother;
        this.generation = mother.generation + 1;
        if ( this.generation > maximumGeneration ) {
            maximumGeneration = this.generation;
        }
        this.x = mother.x;
        this.y = mother.y;
        this.viewAngle = (float) ( MathUtil.random.nextDouble() * MathUtil.PI * 2 );
        try {
            this.brain = mother.brain.cloneFullMesh();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        this.amountOfMemory = mother.amountOfMemory;

        this.inBias = this.brain.getInputNeuronFromName( this.NAME_IN_BIAS );
        this.inFoodValuePosition = this.brain.getInputNeuronFromName( this.NAME_IN_FOOD_VALUE_POSITION );
        this.inFoodValueFeeler = this.brain.getInputNeuronFromName( this.NAME_IN_FOOD_VALUE_FEELER );
        this.inEnergy = this.brain.getInputNeuronFromName( this.NAME_IN_ENERGY );
        this.inAge = this.brain.getInputNeuronFromName( this.NAME_IN_AGE );
        this.inWaterOnFeeler = this.brain.getInputNeuronFromName( this.NAME_IN_WATER_ON_FEELER );
        this.inWaterOnCreature = this.brain.getInputNeuronFromName( this.NAME_IN_WATER_ON_CREATURE );

        this.outBirth = this.brain.getOutputNeuronFromName( this.NAME_OUT_BIRTH );
        this.outRotate = this.brain.getOutputNeuronFromName( this.NAME_OUT_ROTATE );
        this.outForward = this.brain.getOutputNeuronFromName( this.NAME_OUT_FORWARD );
        this.outFeelerAngle = this.brain.getOutputNeuronFromName( this.NAME_OUT_FEELER_ANGLE );
        this.outEat = this.brain.getOutputNeuronFromName( this.NAME_OUT_EAT );

        this.calculateFeelerPosition();
        this.mutateConnections();

        float r = mother.color.getRed() / 255f;
        float g = mother.color.getGreen() / 255f;
        float b = mother.color.getBlue() / 255f;

        r += (float) MathUtil.random.nextDouble() * 0.1f - 0.05f;
        g += (float) MathUtil.random.nextDouble() * 0.1f - 0.05f;
        b += (float) MathUtil.random.nextDouble() * 0.1f - 0.05f;

        r = MathUtil.clamp( r );
        g = MathUtil.clamp( g );
        b = MathUtil.clamp( b );

        this.color = new Color( r, g, b );
    }

    private void mutateConnections() {
        for ( int i = 0; i < 10; i++ ) {
            this.brain.randomMutation( 0.1f );
        }
    }

    public void readSensors() {
        this.brain.invalidate();

        Tile creatureTile = this.level.getTile( (int) this.x / 16, (int) this.y / 16 );
        Tile feelerTile = this.level.getTile( (int) this.feelerX / 16, (int) this.feelerY / 16 );

        this.inBias.setValue( 1f );
        if ( this.x / 16 > 0 && this.x / 16 <= this.level.width && this.y / 16 > 0 && this.y / 16 <= this.level.height ) {
            this.inFoodValuePosition.setValue( this.level.foodValues[(int) ( this.x / 16 )][(int) ( this.y / 16 )] / this.level.MAXIMUM_FOOD_PER_TILE );
        } else {
            this.inFoodValuePosition.setValue( 0f );
        }
        if ( this.feelerX / 16 > 0 && this.feelerX / 16 <= this.level.width && this.feelerY / 16 > 0 && this.feelerY / 16 <= this.level.height ) {
            this.inFoodValueFeeler.setValue( this.level.foodValues[(int) ( this.x / 16 )][(int) ( this.y / 16 )] / this.level.MAXIMUM_FOOD_PER_TILE );
        } else {
            this.inFoodValueFeeler.setValue( 0f );
        }
        this.inEnergy.setValue( ( this.energy - this.MINIMUM_SURVIVAL_ENERGY ) / ( this.START_ENERGY - this.MINIMUM_SURVIVAL_ENERGY ) );
        this.inAge.setValue( this.age / 10f );
        this.inWaterOnFeeler.setValue( feelerTile.isGrass() ? 0f : 1f );
        this.inWaterOnCreature.setValue( creatureTile.isGrass() ? 0f : 1f );
    }

    public void act() {
        Tile tile = this.level.getTile( (int) this.x / 16, (int) this.y / 16 );
        float costMult = this.calculateCostMultiplier( tile );
        this.actRotate( costMult );
        this.actMove( costMult );
        this.actBirth();
        this.actFeelerRotate();
        this.actEat( costMult, tile );

        this.age += this.level.TIME_PER_TICK;

        if ( this.age > oldestCreatureEver.age ) {
            oldestCreatureEver = this;
        }

        if ( this.energy < 100 ) {
            this.kill( tile );
        }
    }

    private void kill( Tile tile ) {
        this.level.creatureFactory.removeCreature( this );
    }

    private void actRotate( float costMult ) {
        float rotateForce = MathUtil.clampNegativePosition( this.outRotate.getValue() );
        this.viewAngle += rotateForce / 10;
        this.energy -= MathUtil.abs( rotateForce * this.COST_ROTATE * costMult );
    }

    private void actMove( float costMult ) {
        float forwardX = MathUtil.sin( this.viewAngle ) * this.MOVE_SPEED;
        float forwardY = MathUtil.cos( this.viewAngle ) * this.MOVE_SPEED;
        float forwardForce = MathUtil.clampNegativePosition( this.outForward.getValue() );
        forwardX *= forwardForce;
        forwardY *= forwardForce;
        this.x += forwardX;
        this.y += forwardY;
        this.energy -= MathUtil.abs( forwardForce * this.COST_WALK * costMult );
    }

    private void actBirth() {
        float birthWish = this.outBirth.getValue();
        if ( birthWish > 0 ) {
            this.tryToGiveBirth();
        }
    }

    private void actFeelerRotate() {
        this.feelerAngle = MathUtil.clampNegativePosition( this.outFeelerAngle.getValue() ) * MathUtil.PI;
        this.calculateFeelerPosition();
    }

    private void actEat( float costMult, Tile creatureTile ) {
        float eatWish = MathUtil.clamp( this.outEat.getValue() );
        if ( eatWish > 0 ) {
            this.eat( eatWish, creatureTile );
            this.energy -= eatWish * this.COST_EAT * costMult;
        }
    }

    private void eat( float eatWish, Tile tile ) {
        if ( this.x / 16 > 0 && this.x / 16 <= this.level.width && this.y / 16 > 0 && this.y / 16 <= this.level.height ) {
            if ( tile.isGrass() ) {
                float foodValue = this.level.foodValues[(int) this.x / 16][(int) this.y / 16];
                if ( foodValue > 0 ) {
                    if ( foodValue > this.GAIN_EAT * eatWish ) {
                        this.energy += this.GAIN_EAT * eatWish;
                        this.level.foodValues[(int) this.x / 16][(int) this.y / 16] -= this.GAIN_EAT;
                    } else {
                        this.energy += foodValue;
                        this.level.foodValues[(int) this.x / 16][(int) this.y / 16] = 0;
                    }
                }
            }
        }
    }

    private void tryToGiveBirth() {
        if ( this.isAbleToGiveBirth() ) {
            this.giveBirth();
        }
    }

    private void giveBirth() {
        Creature child = new TestCreature( this.level, this );
        this.children.add( child );
        this.level.creatureFactory.addCreature( new TestCreature( this.level, this ) );
        this.energy -= this.START_ENERGY;
    }

    private boolean isAbleToGiveBirth() {
        return this.energy > this.START_ENERGY + this.MINIMUM_SURVIVAL_ENERGY * 1.1f;
    }

    private void calculateFeelerPosition() {
        float angle = this.feelerAngle + this.viewAngle;
        float x = MathUtil.sin( angle ) * 12;
        float y = MathUtil.cos( angle ) * 12;
        this.feelerX = this.x + x;
        this.feelerY = this.y + y;
    }

    private float calculateCostMultiplier( Tile creatureTile ) {
        return this.age * ( creatureTile.isGrass() ? 1 : 2 );
    }

    public abstract void update();

    public abstract void render( Graphics g, int xOffset, int yOffset );

}
