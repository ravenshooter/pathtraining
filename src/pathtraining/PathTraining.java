/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pathtraining;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import pathtraining.grapheditingtools.Graph;
import pathtraining.grapheditingtools.EdgeAdder;
import pathtraining.grapheditingtools.NodeAdder;
import pathtraining.grapheditingtools.NodeMover;
import pathtraining.graphics.Camera;
import pathtraining.graphics.CameraController;
import pathtraining.graphics.Canvas;
import pathtraining.graphics.GraphRenderer;
import pathtraining.graphics.RenderMaster;
import pathtraining.graphics.SpriteStore;

/**
 *
 * @author Stephan
 */
public class PathTraining {

    
    public static boolean isEditMode = true;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String map = "campus_groß";
        /**
        for(String arg : args){
            if(arg.equals("-edit")){
                isEditMode = true;
            }else if (arg.startsWith("-map")){
                map = arg.substring(4);
            }
        }*/
        new PathTraining(map);
    }
    
    
    private NodeAdder nAdd;
    private EdgeAdder eAdd;
    private NodeMover nMover;
    private Canvas canvas;
            
    private MouseListener currentTool;
    
    RenderMaster render;
    
    final Graph graph;
           
    private Camera camera;
    
    public PathTraining(String mapName){
        this(mapName, false);
    }
    
    public PathTraining(String mapName, boolean edit){
    
        isEditMode = edit;

        Window window = new Window(isEditMode);
        camera = new Camera(0, 0, 1200, 800);
        
        CameraController camController = new CameraController(window.getContentPane(),camera);
        
        
        graph = new Graph();
        Map map = new Map(graph, SpriteStore.get().getSprite("/maps/"+mapName+".png"), camera);
        
        
        
        render = new RenderMaster(camera, graph, map.getDrawable());
        
        
        //create canvas
        canvas = new Canvas(render,camera, new pathtraining.graphics.MouseMotionListener(map, window.getInfoPanel(),render.getGraphR()));
        canvas.setDoubleBuffered(true);
        window.setCanvas(canvas);
        canvas.start();
        
        
        //create tools
        nAdd = new NodeAdder(graph, render.getGraphR(), camera);
        eAdd = new EdgeAdder(graph, map);
        nMover = new NodeMover(map,graph);
        
        window.getContentPane().addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                camera.setSize(canvas.getSize());
            }

        });
        
        window.getAddEdgeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTool(eAdd);
            }
        });
        
        window.getAddNodeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTool(nAdd);
            }
        });
        
        window.getMoveNodeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTool(nMover);
            }
        });
        
        window.getSaveGraphButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GraphSaver(graph);
            }
        });
        
        window.getShowDiscoveryTimeBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ((GraphRenderer) render.getGraphR()).setIsShowDiscoveryTime(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
            
        window.getShowFinishTimeBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ((GraphRenderer) render.getGraphR()).setIsShowFinishTime(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
            
        window.getShowDistanceTimeBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ((GraphRenderer) render.getGraphR()).setIsShowDistance(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        
        /*
        window.getShowPiBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ((GraphRenderer) render.getGraphR()).setIsShowPi(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        */
        GraphLoader loader = new GraphLoader();
        loader.loadGraph("/maps/"+mapName+".graph", graph, nAdd, eAdd);
        
    }
    
    public void setTool(MouseListener m) {
        canvas.removeMouseListener(currentTool);
        if( currentTool instanceof MouseMotionListener){
            canvas.removeMouseMotionListener((MouseMotionListener)currentTool);
        }
        
        currentTool = m;
        
        canvas.addMouseListener(currentTool);
        if( currentTool instanceof MouseMotionListener){
            canvas.addMouseMotionListener((MouseMotionListener)currentTool);
        }
        
        canvas.requestFocusInWindow();
    }

    public Graph getGraph() {
        return graph;
    }
    
    
    
    
}
