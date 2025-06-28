package com.avogine.render.data;

import java.util.*;

import org.joml.Matrix4f;

/**
 *
 */
public class Node {
	private final List<Node> children;
	
	private final String name;
	
	private final Node parent;
	
	private Matrix4f nodeTransformation;
	
	public Node(String name, Node parent, Matrix4f nodeTransformation) {
		this.name = name;
		this.parent = parent;
		this.nodeTransformation = nodeTransformation;
		this.children = new ArrayList<>();
	}
	
	public void addChild(Node node) {
		children.add(node);
	}
	
	/**
	 * @return the children
	 */
	public List<Node> getChildren() {
		return children;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the nodeTransformation
	 */
	public Matrix4f getNodeTransformation() {
		return nodeTransformation;
	}
	
	/**
	 * @return the parent
	 */
	public Node getParent() {
		return parent;
	}
}
