package com.plusls.ommc.compat;

import org.objectweb.asm.tree.ClassNode;

import java.util.function.Predicate;

// make java compiler happy
public interface CustomDepPredicate extends Predicate<ClassNode> {
}
