/**
 * Inspired by CtScanner in INRIA/spoon, and it uses the following licence
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package fr.quentin.refSolver;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.cu.CompilationUnitImpl;
import spoon.support.reflect.cu.position.PartialSourcePositionImpl;
import spoon.support.reflect.cu.position.SourcePositionImpl;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;

/**
 * This visitor implements a deep-search scan on the model.
 *
 * Ensures that all children nodes are visited once, a visit means three method
 * calls, one call to "enter", one call to "exit" and one call to scan.
 *
 * Is used by the processing and filtering engine.
 */
public class CtRefHolderVisitor implements CtVisitor {
	Map<CtElement, Set<CtElement>> res = new IdentityHashMap<>();
	int totalrefs = 0;
	boolean match_typeof = false;

	/**
	 * Default constructor.
	 */
	public CtRefHolderVisitor() {
	}

	/**
	 * This method is upcalled by the TODO
	 */
	protected void matched(CtElement e, CtReference r) {
		if (e == null)
			return;
		if (r == null)
			return;
		if (r instanceof CtArrayTypeReference) {
			r = ((CtArrayTypeReference<?>) r).getArrayType();
		}
		CtElement d = r.getDeclaration();
		if (d != null) {
			totalrefs++;
			res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
			res.get(d).add(e);
		}
	}

	private <T extends CtReference, C extends Collection<T>> void matched(CtElement e, C c) {
		if (e == null)
			return;
		if (c == null)
			return;
		for (CtReference r : c) {
			matched(e, r);
		}
	}

	private void decl(CtElement e) {
		if (e == null)
			return;
		if (e instanceof CtParameter) {
			CtParameter<?> aaa = (CtParameter<?>) e;
			// if (aaa.getSimpleName().equals("msg"))
			// System.err.println("delc(" + aaa.getParent().toStringDebug());
		}
		res.putIfAbsent(e, Collections.newSetFromMap(new IdentityHashMap<>()));
	}

	private void ref(CtReference e) {
		if (e == null)
			return;
		CtElement d = e.getDeclaration();
		if (d == null)
			return;
		// if (d instanceof CtParameter) {
		// CtParameter<?> aaa = (CtParameter<?>) d;
		// // if (aaa.getSimpleName().equals("msg"))
		// // System.err.println("ref(" + aaa.getParent().toStringDebug());
		// }
		res.putIfAbsent(d, Collections.newSetFromMap(new IdentityHashMap<>()));
	}

	/**
	 * Generically scans a collection of meta-model elements.
	 */
	public void scan(CtRole role, Collection<? extends CtElement> elements) {
		if (elements != null) {
			// we use defensive copy so as to be able to change the class while scanning
			// otherwise one gets a ConcurrentModificationException
			for (CtElement e : new ArrayList<>(elements)) {
				scan(role, e);
			}
		}
	}

	/**
	 * Generically scans a Map of meta-model elements.
	 */
	public void scan(CtRole role, Map<String, ? extends CtElement> elements) {
		if (elements != null) {
			for (CtElement obj : elements.values()) {
				scan(role, obj);
			}
		}
	}

	/**
	 * Generically scans a collection of meta-model elements.
	 */
	public void scan(Collection<? extends CtElement> elements) {
		scan(null, elements);
	}

	/**
	 * Generically scans a meta-model element.
	 */
	public void scan(CtRole role, CtElement element) {
		// if (element!=null && element.toString().endsWith(".class"))
		// System.err.println();
		scan(element);
	}

	/**
	 * Generically scans a meta-model element.
	 */
	public void scan(CtElement element) {
		if (element != null) {
			element.accept(this);
		}
	}

	public <A extends Annotation> void visitCtAnnotation(final CtAnnotation<A> annotation) {
		// matched(annotation, annotation.getAnnotationType());
		CtTypeReference<A> t = annotation.getAnnotationType();

		SourcePosition p = annotation.getPosition();
		if (p == null || !p.isValidPosition() || p instanceof PartialSourcePositionImpl) {
		} else if (t.getPosition() == null || !t.getPosition().isValidPosition()) {
			// System.err.println(p);
			int e = p.getCompilationUnit().getOriginalSourceCode().substring(p.getSourceStart() + 1, p.getSourceEnd())
					.indexOf("(");
			t.setPosition(new SourcePositionImpl(
					p.getCompilationUnit(),
					p.getSourceStart() + 1,
					e == -1 ? p.getSourceEnd() : p.getSourceStart() + 1 + e - 1,
					// p.getSourceEnd(),
					((CompilationUnitImpl) p.getCompilationUnit()).getLineSeparatorPositions()));
		}
		scan(CtRole.TYPE, annotation.getType());
		scan(CtRole.ANNOTATION_TYPE, t);
		scan(CtRole.ANNOTATION, annotation.getAnnotations());
		scan(CtRole.VALUE, annotation.getValues());
	}

	/**
	 * Generically scans an object that can be an element, a reference, or a
	 * collection of those.
	 */
	public void scan(Object o) {
		scan(null, o);
	}

	/**
	 * Generically scans an object that can be an element, a reference, or a
	 * collection of those.
	 */
	public void scan(CtRole role, Object o) {
		if (o instanceof CtElement) {
			scan(role, ((CtElement) (o)));
		}
		if (o instanceof Collection<?>) {
			scan(role, (Collection<? extends CtElement>) o);
		}
		if (o instanceof Map<?, ?>) {
			scan(role, (Map<String, ? extends CtElement>) o);
		}
	}

	public <A extends Annotation> void visitCtAnnotationType(final CtAnnotationType<A> annotationType) {
		// matched(annotationType,ref);
		decl(annotationType);
		scan(CtRole.ANNOTATION, annotationType.getAnnotations());
		scan(CtRole.TYPE_MEMBER, annotationType.getTypeMembers());
	}

	public void visitCtAnonymousExecutable(final CtAnonymousExecutable anonymousExec) {
		// matched(anonymousExec,ref);
		scan(CtRole.ANNOTATION, anonymousExec.getAnnotations());
		scan(CtRole.BODY, anonymousExec.getBody());
	}

	@Override
	public <T> void visitCtArrayRead(final CtArrayRead<T> arrayRead) {
		// matched(arrayRead,ref);
		scan(CtRole.ANNOTATION, arrayRead.getAnnotations());
		scan(CtRole.TYPE, arrayRead.getType());
		scan(CtRole.CAST, arrayRead.getTypeCasts());
		scan(CtRole.TARGET, arrayRead.getTarget());
		scan(CtRole.EXPRESSION, arrayRead.getIndexExpression());
	}

	@Override
	public <T> void visitCtArrayWrite(final CtArrayWrite<T> arrayWrite) {
		// matched(arrayWrite,ref);
		scan(CtRole.ANNOTATION, arrayWrite.getAnnotations());
		scan(CtRole.TYPE, arrayWrite.getType());
		scan(CtRole.CAST, arrayWrite.getTypeCasts()); // TODO maybe
		scan(CtRole.TARGET, arrayWrite.getTarget());
		scan(CtRole.EXPRESSION, arrayWrite.getIndexExpression());
	}

	public <T> void visitCtArrayTypeReference(final CtArrayTypeReference<T> reference) {
		// matched(reference,ref);
		scan(CtRole.PACKAGE_REF, reference.getPackage());
		scan(CtRole.DECLARING_TYPE, reference.getDeclaringType());
		scan(CtRole.TYPE, reference.getComponentType());
		scan(CtRole.TYPE_ARGUMENT, reference.getActualTypeArguments());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
	}

	public <T> void visitCtAssert(final CtAssert<T> asserted) {
		// matched(asserted,ref);
		scan(CtRole.ANNOTATION, asserted.getAnnotations());
		scan(CtRole.CONDITION, asserted.getAssertExpression());
		scan(CtRole.EXPRESSION, asserted.getExpression());
	}

	public <T, A extends T> void visitCtAssignment(final CtAssignment<T, A> assignement) {
		// matched(assignement,ref);
		scan(CtRole.ANNOTATION, assignement.getAnnotations());
		scan(CtRole.TYPE, assignement.getType());
		scan(CtRole.CAST, assignement.getTypeCasts());
		scan(CtRole.ASSIGNED, assignement.getAssigned());
		scan(CtRole.ASSIGNMENT, assignement.getAssignment());
	}

	public <T> void visitCtBinaryOperator(final CtBinaryOperator<T> operator) {
		// matched(operator,ref);
		scan(CtRole.ANNOTATION, operator.getAnnotations());
		scan(CtRole.TYPE, operator.getType());
		scan(CtRole.CAST, operator.getTypeCasts());
		scan(CtRole.LEFT_OPERAND, operator.getLeftHandOperand());
		scan(CtRole.RIGHT_OPERAND, operator.getRightHandOperand());
	}

	public <R> void visitCtBlock(final CtBlock<R> block) {
		// matched(block,ref);
		scan(CtRole.ANNOTATION, block.getAnnotations());
		scan(CtRole.STATEMENT, block.getStatements());
	}

	public void visitCtBreak(final CtBreak breakStatement) {
		// matched(breakStatement,ref);
		scan(CtRole.ANNOTATION, breakStatement.getAnnotations());
	}

	public <S> void visitCtCase(final CtCase<S> caseStatement) {
		// matched(caseStatement,ref);
		scan(CtRole.ANNOTATION, caseStatement.getAnnotations());
		scan(CtRole.EXPRESSION, caseStatement.getCaseExpression());
		scan(CtRole.STATEMENT, caseStatement.getStatements());
	}

	public void visitCtCatch(final CtCatch catchBlock) {
		// matched(catchBlock,ref);
		scan(CtRole.ANNOTATION, catchBlock.getAnnotations());
		scan(CtRole.PARAMETER, catchBlock.getParameter());
		scan(CtRole.BODY, catchBlock.getBody());
	}

	public <T> void visitCtClass(final CtClass<T> ctClass) {
		// matched(ctClass,
		// ctClass.getSuperInterfaces().stream().collect(Collectors.toList()));
		decl(ctClass);
		// matched(ctClass, ctClass.getSuperclass());
		// if (ctClass.getSimpleName().equals("JLSViolation"))
		// System.err.println(ctClass.getTypeMembers());
		scan(CtRole.TYPE_MEMBER, ctClass.getTypeMembers());
		scan(CtRole.ANNOTATION, ctClass.getAnnotations());
		scan(CtRole.SUPER_TYPE, ctClass.getSuperclass());
		scan(CtRole.INTERFACE, ctClass.getSuperInterfaces());
		scan(CtRole.TYPE_PARAMETER, ctClass.getFormalCtTypeParameters());
		scan(CtRole.TYPE_MEMBER, ctClass.getTypeMembers());
	}

	@Override
	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		decl(typeParameter);
		// matched(typeParameter, typeParameter.getSuperclass());
		scan(CtRole.ANNOTATION, typeParameter.getAnnotations());
		scan(CtRole.SUPER_TYPE, typeParameter.getSuperclass());
	}

	public <T> void visitCtConditional(final CtConditional<T> conditional) {
		// matched(conditional,ref);
		// decl(conditional);
		scan(CtRole.TYPE, conditional.getType());
		scan(CtRole.ANNOTATION, conditional.getAnnotations());
		scan(CtRole.CONDITION, conditional.getCondition());
		scan(CtRole.THEN, conditional.getThenExpression());
		scan(CtRole.ELSE, conditional.getElseExpression());
		scan(CtRole.CAST, conditional.getTypeCasts());
	}

	public <T> void visitCtConstructor(final CtConstructor<T> c) {
		matched(c, c.getType());
		// if (c.getDeclaringType().getSimpleName().equals("JLSViolation"))
		// System.err.println(c.getSignature() + c.getParameters());
		decl(c);
		scan(CtRole.ANNOTATION, c.getAnnotations());
		scan(CtRole.PARAMETER, c.getParameters());
		scan(CtRole.THROWN, c.getThrownTypes());
		scan(CtRole.TYPE_PARAMETER, c.getFormalCtTypeParameters());
		scan(CtRole.BODY, c.getBody());
	}

	public void visitCtContinue(final CtContinue continueStatement) {
		scan(CtRole.ANNOTATION, continueStatement.getAnnotations());
	}

	public void visitCtDo(final CtDo doLoop) {
		scan(CtRole.ANNOTATION, doLoop.getAnnotations());
		scan(CtRole.EXPRESSION, doLoop.getLoopingExpression());
		scan(CtRole.BODY, doLoop.getBody());
	}

	public <T extends Enum<?>> void visitCtEnum(final CtEnum<T> ctEnum) {
		matched(ctEnum, ctEnum.getSuperInterfaces());
		decl(ctEnum);
		scan(CtRole.ANNOTATION, ctEnum.getAnnotations());
		scan(CtRole.INTERFACE, ctEnum.getSuperInterfaces());
		scan(CtRole.TYPE_MEMBER, ctEnum.getTypeMembers());
		scan(CtRole.VALUE, ctEnum.getEnumValues());
	}

	public <T> void visitCtExecutableReference(final CtExecutableReference<T> reference) {
		matched(reference, reference.getDeclaringType());
		scan(CtRole.DECLARING_TYPE, reference.getDeclaringType());
		scan(CtRole.TYPE, reference.getType());
		scan(CtRole.ARGUMENT_TYPE, reference.getParameters());
		scan(CtRole.TYPE_ARGUMENT, reference.getActualTypeArguments());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
	}

	public <T> void visitCtField(final CtField<T> f) {
		if (match_typeof) {
			matched(f, f.getType());
		}
		decl(f);
		scan(CtRole.ANNOTATION, f.getAnnotations());
		CtTypeReference<T> t = f.getType();
		SourcePosition p = f.getPosition();
		if (p == null || !p.isValidPosition() || p instanceof PartialSourcePositionImpl) {
		} else if (t.getPosition() == null || !t.getPosition().isValidPosition()) {
			// System.err.println(p);
			t.setPosition(new SourcePositionImpl(
					p.getCompilationUnit(),
					p.getSourceStart(),
					p.getSourceEnd(),
					((CompilationUnitImpl) p.getCompilationUnit()).getLineSeparatorPositions()));
		}
		scan(CtRole.TYPE, t);
		scan(CtRole.DEFAULT_EXPRESSION, f.getDefaultExpression());
	}

	@Override
	public <T> void visitCtEnumValue(final CtEnumValue<T> enumValue) {
		matched(enumValue, enumValue.getType());
		decl(enumValue);
		scan(CtRole.ANNOTATION, enumValue.getAnnotations());
		scan(CtRole.TYPE, enumValue.getType());
		scan(CtRole.DEFAULT_EXPRESSION, enumValue.getDefaultExpression());
	}

	@Override
	public <T> void visitCtThisAccess(final CtThisAccess<T> thisAccess) {
		matched(thisAccess, thisAccess.getType());
		scan(CtRole.ANNOTATION, thisAccess.getAnnotations());
		scan(CtRole.TYPE, thisAccess.getType());
		scan(CtRole.CAST, thisAccess.getTypeCasts());
		CtExpression<?> target = thisAccess.getTarget();
		// if (thisAccess.getPosition().isValidPosition()
		// && target != null
		// && !target.isImplicit()
		// && (target.getPosition() == null
		// || target.getPosition() instanceof NoSourcePosition
		// || target.getPosition().isValidPosition() == false)) {
		// // System.err.println(fieldRead.getPosition());
		// // System.err.println(target.getPosition());
		// // System.err.println(fieldRead.getVariable().getSimpleName());
		// // System.err.println(fieldRead.toString());
		// // System.err.println(fieldRead.getPosition().getSourceStart());
		// // System.err.println(fieldRead.getPosition().getSourceEnd());
		// SourcePosition position = new SourcePositionImpl(
		// thisAccess.getPosition().getCompilationUnit(),
		// thisAccess.getPosition().getSourceStart(),
		// thisAccess.getPosition().getSourceEnd() - "this".length(),
		// ((CompilationUnitImpl)
		// thisAccess.getPosition().getCompilationUnit()).getLineSeparatorPositions());
		// target.setPosition(position);
		// }
		scan(CtRole.TARGET, target);
	}

	public <T> void visitCtAnnotationFieldAccess(final CtAnnotationFieldAccess<T> annotationFieldAccess) {
		matched(annotationFieldAccess, annotationFieldAccess.getTypeCasts());
		matched(annotationFieldAccess, annotationFieldAccess.getVariable());
		scan(CtRole.ANNOTATION, annotationFieldAccess.getAnnotations());
		scan(CtRole.CAST, annotationFieldAccess.getTypeCasts());
		scan(CtRole.TARGET, annotationFieldAccess.getTarget());
		scan(CtRole.VARIABLE, annotationFieldAccess.getVariable());
	}

	public <T> void visitCtFieldReference(final CtFieldReference<T> reference) {
		ref(reference);
		scan(CtRole.DECLARING_TYPE, reference.getDeclaringType());
		scan(CtRole.TYPE, reference.getType());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
	}

	public void visitCtFor(final CtFor forLoop) {
		scan(CtRole.ANNOTATION, forLoop.getAnnotations());
		scan(CtRole.FOR_INIT, forLoop.getForInit());
		scan(CtRole.EXPRESSION, forLoop.getExpression());
		scan(CtRole.FOR_UPDATE, forLoop.getForUpdate());
		scan(CtRole.BODY, forLoop.getBody());
	}

	public void visitCtForEach(final CtForEach foreach) {
		scan(CtRole.ANNOTATION, foreach.getAnnotations());
		scan(CtRole.FOREACH_VARIABLE, foreach.getVariable());
		scan(CtRole.EXPRESSION, foreach.getExpression());
		scan(CtRole.BODY, foreach.getBody());
	}

	public void visitCtIf(final CtIf ifElement) {
		scan(CtRole.ANNOTATION, ifElement.getAnnotations());
		scan(CtRole.CONDITION, ifElement.getCondition());
		scan(CtRole.THEN, ((CtStatement) (ifElement.getThenStatement())));
		scan(CtRole.ELSE, ((CtStatement) (ifElement.getElseStatement())));
	}

	public <T> void visitCtInterface(final CtInterface<T> intrface) {
		// matched(intrface, intrface.getSuperInterfaces());
		decl(intrface);
		scan(CtRole.ANNOTATION, intrface.getAnnotations());
		scan(CtRole.INTERFACE, intrface.getSuperInterfaces());
		scan(CtRole.TYPE_PARAMETER, intrface.getFormalCtTypeParameters());
		scan(CtRole.TYPE_MEMBER, intrface.getTypeMembers());
	}

	public <T> void visitCtInvocation(final CtInvocation<T> invocation) {
		matched(invocation, invocation.getExecutable());
		if (invocation.toString().startsWith("this(")) {
			CtTypeReference<T> t = invocation.getExecutable().getType();
			SourcePosition p = invocation.getPosition();
			t.setPosition(new SourcePositionImpl(
					p.getCompilationUnit(),
					p.getSourceStart(),
					p.getSourceStart()+4-1,
					((CompilationUnitImpl) p.getCompilationUnit()).getLineSeparatorPositions()));
			matched(t, t);
		}
		// TODO check if match explicit ref to type
		scan(CtRole.ANNOTATION, invocation.getAnnotations());
		scan(CtRole.CAST, invocation.getTypeCasts());
		scan(CtRole.TARGET, invocation.getTarget());
		scan(CtRole.EXECUTABLE_REF, invocation.getExecutable());
		scan(CtRole.ARGUMENT, invocation.getArguments());
	}

	public <T> void visitCtLiteral(final CtLiteral<T> literal) {
		scan(CtRole.ANNOTATION, literal.getAnnotations());
		scan(CtRole.TYPE, literal.getType());
		scan(CtRole.CAST, literal.getTypeCasts());
	}

	public <T> void visitCtLocalVariable(final CtLocalVariable<T> localVariable) {
		if (match_typeof)
			matched(localVariable, localVariable.getType());
		decl(localVariable);
		scan(CtRole.ANNOTATION, localVariable.getAnnotations());
		CtTypeReference<T> t = localVariable.getType();
		if (t.getPosition() == null || !t.getPosition().isValidPosition()) {
			// System.err.println(t.getPosition());
			SourcePosition p = localVariable.getPosition();
			t.setPosition(new SourcePositionImpl(
					p.getCompilationUnit(),
					p.getSourceStart(),
					(p.getSourceEnd()),
					((CompilationUnitImpl) p.getCompilationUnit()).getLineSeparatorPositions()));
		}
		scan(CtRole.TYPE, t);
		scan(CtRole.DEFAULT_EXPRESSION, localVariable.getDefaultExpression());
	}

	public <T> void visitCtLocalVariableReference(final CtLocalVariableReference<T> reference) {
		ref(reference);
		// scan(CtRole.TYPE, reference.getType());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
	}

	public <T> void visitCtCatchVariable(final CtCatchVariable<T> catchVariable) {
		// matched(catchVariable, catchVariable.getMultiTypes());
		decl(catchVariable);
		scan(CtRole.ANNOTATION, catchVariable.getAnnotations());
		scan(CtRole.MULTI_TYPE, catchVariable.getMultiTypes()); // TODO make sure of position
	}

	public <T> void visitCtCatchVariableReference(final CtCatchVariableReference<T> reference) {
		ref(reference);
		// scan(CtRole.TYPE, reference.getType());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
	}

	public <T> void visitCtMethod(final CtMethod<T> m) {
		if (match_typeof) {
			matched(m, m.getType());
		}
		decl(m);
		scan(CtRole.ANNOTATION, m.getAnnotations());
		scan(CtRole.TYPE_PARAMETER, m.getFormalCtTypeParameters());
		CtTypeReference<T> t = m.getType();
		SourcePosition p = m.getPosition();
		if (p == null || !p.isValidPosition() || p instanceof PartialSourcePositionImpl) {
		} else if (t.getPosition() == null || !t.getPosition().isValidPosition()) {
			// System.err.println(p);
			t.setPosition(new SourcePositionImpl(
					p.getCompilationUnit(),
					p.getSourceStart(),
					p.getSourceEnd(),
					((CompilationUnitImpl) p.getCompilationUnit()).getLineSeparatorPositions()));
		}
		scan(CtRole.TYPE, t);
		scan(CtRole.PARAMETER, m.getParameters());
		scan(CtRole.THROWN, m.getThrownTypes());
		scan(CtRole.BODY, m.getBody());
	}

	@Override
	public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {
		matched(annotationMethod, annotationMethod.getType());
		decl(annotationMethod);
		scan(CtRole.ANNOTATION, annotationMethod.getAnnotations());
		scan(CtRole.TYPE, annotationMethod.getType());
		scan(CtRole.DEFAULT_EXPRESSION, annotationMethod.getDefaultExpression());
	}

	public <T> void visitCtNewArray(final CtNewArray<T> newArray) {
		// matched(newArray, newArray.getType());
		matched(newArray, newArray.getTypeCasts());
		scan(CtRole.ANNOTATION, newArray.getAnnotations());
		CtTypeReference<T> t = newArray.getType();
		t.setPosition(newArray.getPosition());
		scan(CtRole.TYPE, t);
		scan(CtRole.CAST, newArray.getTypeCasts());
		scan(CtRole.EXPRESSION, newArray.getElements());
		scan(CtRole.DIMENSION, newArray.getDimensionExpressions());
	}

	@Override
	public <T> void visitCtConstructorCall(final CtConstructorCall<T> ctConstructorCall) {
		matched(ctConstructorCall, ctConstructorCall.getExecutable());
		matched(ctConstructorCall, ctConstructorCall.getType());
		scan(CtRole.ANNOTATION, ctConstructorCall.getAnnotations());
		scan(CtRole.CAST, ctConstructorCall.getTypeCasts());
		// scan(CtRole.EXECUTABLE_REF, ctConstructorCall.getExecutable());
		// scan(CtRole.TARGET, ctConstructorCall.getTarget());
		scan(CtRole.ARGUMENT, ctConstructorCall.getArguments());
	}

	public <T> void visitCtNewClass(final CtNewClass<T> newClass) {
		matched(newClass, newClass.getExecutable());
		matched(newClass, newClass.getTypeCasts());
		scan(CtRole.ANNOTATION, newClass.getAnnotations());
		scan(CtRole.CAST, newClass.getTypeCasts());
		// scan(CtRole.EXECUTABLE_REF, newClass.getExecutable());
		// scan(CtRole.TARGET, newClass.getTarget());
		scan(CtRole.ARGUMENT, newClass.getArguments());
		scan(CtRole.NESTED_TYPE, newClass.getAnonymousClass());
	}

	@Override
	public <T> void visitCtLambda(final CtLambda<T> lambda) {
		matched(lambda, lambda.getType());
		matched(lambda, lambda.getTypeCasts());
		scan(CtRole.ANNOTATION, lambda.getAnnotations());
		scan(CtRole.TYPE, lambda.getType());
		scan(CtRole.CAST, lambda.getTypeCasts());
		scan(CtRole.PARAMETER, lambda.getParameters());
		scan(CtRole.BODY, lambda.getBody());
		scan(CtRole.EXPRESSION, lambda.getExpression());
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(
			final CtExecutableReferenceExpression<T, E> expression) {
		matched(expression, expression.getExecutable());
		matched(expression, expression.getTypeCasts());
		scan(CtRole.ANNOTATION, expression.getAnnotations());
		scan(CtRole.TYPE, expression.getType());
		scan(CtRole.CAST, expression.getTypeCasts());
		scan(CtRole.EXECUTABLE_REF, expression.getExecutable());
		scan(CtRole.TARGET, expression.getTarget());
	}

	public <T, A extends T> void visitCtOperatorAssignment(final CtOperatorAssignment<T, A> assignment) {
		matched(assignment, assignment.getTypeCasts());
		scan(CtRole.ANNOTATION, assignment.getAnnotations());
		scan(CtRole.TYPE, assignment.getType());
		scan(CtRole.CAST, assignment.getTypeCasts());
		scan(CtRole.ASSIGNED, assignment.getAssigned());
		scan(CtRole.ASSIGNMENT, assignment.getAssignment());
	}

	public void visitCtPackage(final CtPackage ctPackage) {
		scan(CtRole.ANNOTATION, ctPackage.getAnnotations());
		scan(CtRole.SUB_PACKAGE, ctPackage.getPackages());
		scan(CtRole.CONTAINED_TYPE, ctPackage.getTypes());
	}

	public void visitCtPackageReference(final CtPackageReference reference) {
		scan(CtRole.ANNOTATION, reference.getAnnotations());
	}

	public <T> void visitCtParameter(final CtParameter<T> parameter) {
		decl(parameter);
		// if (parameter.toString().contains("..."))
		// System.err.println();
		if (this.match_typeof)
			matched(parameter, parameter.getType());

		scan(CtRole.ANNOTATION, parameter.getAnnotations());

		CtTypeReference<T> t = parameter.getType();
		SourcePosition p = parameter.getPosition();
		if (parameter.isVarArgs()) {
			CtTypeReference tmp = ((CtArrayTypeReference) t).getArrayType();
			SourcePosition pp = tmp.getPosition();
			if (pp == null || !pp.isValidPosition() || pp instanceof PartialSourcePositionImpl) {
				SourcePosition ppp = t.getPosition();
				if (ppp == null || !ppp.isValidPosition() || ppp instanceof PartialSourcePositionImpl) {
				} else {
					tmp.setPosition(new SourcePositionImpl(
							ppp.getCompilationUnit(),
							ppp.getSourceStart(),
							ppp.getSourceEnd() - 3,
							((CompilationUnitImpl) p.getCompilationUnit()).getLineSeparatorPositions()));
				}
			}
			t = tmp;
		} else if (p == null || !p.isValidPosition() || p instanceof PartialSourcePositionImpl) {
		} else if (t==null || t.isImplicit() || parameter.isInferred()) {
			return;
		} else if (t.getPosition() == null || !t.getPosition().isValidPosition()) {
			// System.err.println(p);
			t.setPosition(new SourcePositionImpl(
					p.getCompilationUnit(),
					p.getSourceStart(),
					p.getSourceEnd(),
					((CompilationUnitImpl) p.getCompilationUnit()).getLineSeparatorPositions()));
		}
		scan(CtRole.TYPE, t);
	}

	public <T> void visitCtParameterReference(final CtParameterReference<T> reference) {
		ref(reference);
		matched(reference, reference.getType());
		scan(CtRole.TYPE, reference.getType());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
	}

	public <R> void visitCtReturn(final CtReturn<R> returnStatement) {
		scan(CtRole.ANNOTATION, returnStatement.getAnnotations());
		scan(CtRole.EXPRESSION, returnStatement.getReturnedExpression());
	}

	public <R> void visitCtStatementList(final CtStatementList statements) {
		scan(CtRole.ANNOTATION, statements.getAnnotations());
		scan(CtRole.STATEMENT, statements.getStatements());
	}

	public <S> void visitCtSwitch(final CtSwitch<S> switchStatement) {
		scan(CtRole.ANNOTATION, switchStatement.getAnnotations());
		scan(CtRole.EXPRESSION, switchStatement.getSelector());
		scan(CtRole.CASE, switchStatement.getCases());
	}

	public void visitCtSynchronized(final CtSynchronized synchro) {
		scan(CtRole.ANNOTATION, synchro.getAnnotations());
		scan(CtRole.EXPRESSION, synchro.getExpression());
		scan(CtRole.BODY, synchro.getBlock());
	}

	public void visitCtThrow(final CtThrow throwStatement) {
		scan(CtRole.ANNOTATION, throwStatement.getAnnotations());
		scan(CtRole.EXPRESSION, throwStatement.getThrownExpression());
	}

	public void visitCtTry(final CtTry tryBlock) {
		scan(CtRole.ANNOTATION, tryBlock.getAnnotations());
		scan(CtRole.BODY, tryBlock.getBody());
		scan(CtRole.CATCH, tryBlock.getCatchers());
		scan(CtRole.FINALIZER, tryBlock.getFinalizer());
	}

	@Override
	public void visitCtTryWithResource(final CtTryWithResource tryWithResource) {
		scan(CtRole.ANNOTATION, tryWithResource.getAnnotations());
		scan(CtRole.TRY_RESOURCE, tryWithResource.getResources());
		scan(CtRole.BODY, tryWithResource.getBody());
		scan(CtRole.CATCH, tryWithResource.getCatchers());
		scan(CtRole.FINALIZER, tryWithResource.getFinalizer());
	}

	public void visitCtTypeParameterReference(final CtTypeParameterReference ref) {
		ref(ref);
		scan(CtRole.PACKAGE_REF, ref.getPackage());
		scan(CtRole.DECLARING_TYPE, ref.getDeclaringType());
		scan(CtRole.ANNOTATION, ref.getAnnotations());
	}

	@Override
	public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
		scan(CtRole.PACKAGE_REF, wildcardReference.getPackage());
		scan(CtRole.DECLARING_TYPE, wildcardReference.getDeclaringType());
		scan(CtRole.ANNOTATION, wildcardReference.getAnnotations());
		scan(CtRole.BOUNDING_TYPE, wildcardReference.getBoundingType());
	}

	@Override
	public <T> void visitCtIntersectionTypeReference(final CtIntersectionTypeReference<T> reference) {
		ref(reference);
		scan(CtRole.PACKAGE_REF, reference.getPackage());
		scan(CtRole.DECLARING_TYPE, reference.getDeclaringType());
		// TypeReferenceTest fails if actual type arguments are really not set-able on
		// CtIntersectionTypeReference
		scan(CtRole.TYPE_ARGUMENT, reference.getActualTypeArguments());
		scan(CtRole.ANNOTATION, reference.getAnnotations());
		scan(CtRole.BOUND, reference.getBounds());
	}

	public <T> void visitCtTypeReference(final CtTypeReference<T> reference) {
		if (reference.isImplicit())
			return;
		ref(reference);
		List<CtTypeReference<?>> t_args = reference.getActualTypeArguments();
		SourcePosition p = reference.getPosition();
		if (reference.getRoleInParent().equals(CtRole.CAST)) {
			CtTypeReference<T> re = reference.clone();
			re.setPosition(new SourcePositionImpl(
					p.getCompilationUnit(),
					p.getSourceStart() + 1,
					p.getSourceEnd() - 1,
					((CompilationUnitImpl) p.getCompilationUnit()).getLineSeparatorPositions()));
			matched(re, re);
		} else {
			if (p != null && p.isValidPosition() && !(p instanceof PartialSourcePositionImpl)) {
				matched(reference, reference);
			}
		}
		if (p != null && p.isValidPosition() && !(p instanceof PartialSourcePositionImpl)) {
			for (CtTypeReference<?> r : t_args) {
				SourcePosition pp = r.getPosition();
				if (pp == null || !pp.isValidPosition() || pp instanceof PartialSourcePositionImpl) {
					r.setPosition(new SourcePositionImpl(
							p.getCompilationUnit(),
							p.getSourceStart(),
							p.getSourceEnd(),
							((CompilationUnitImpl) p.getCompilationUnit()).getLineSeparatorPositions()));
				}
			}
		}
		scan(CtRole.PACKAGE_REF, reference.getPackage());
		CtTypeReference<?> dt = reference.getDeclaringType();
		SourcePosition dp = reference.getPosition();
		if (dt != null && !dt.isImplicit()) {
			// try {
			// System.err.println(reference);
			// System.err.println(dt);
			// System.err.println(dp);
			// System.err.println(((SourcePositionImpl)dp).getSourceDetails());
			// } catch (Exception e) {
			// //TODO: handle exception
			// }
			if (dp != null && dp.isValidPosition() && !(dp instanceof PartialSourcePositionImpl)) {
				// scan(CtRole.DECLARING_TYPE, dt);
			}
		}

		scan(CtRole.TYPE_ARGUMENT, t_args);
		scan(CtRole.ANNOTATION, reference.getAnnotations());
	}

	@Override
	public <T> void visitCtTypeAccess(final CtTypeAccess<T> typeAccess) {
		matched(typeAccess, typeAccess.getTypeCasts());
		matched(typeAccess, typeAccess.getAccessedType());
		scan(CtRole.ANNOTATION, typeAccess.getAnnotations());
		scan(CtRole.CAST, typeAccess.getTypeCasts());
		scan(CtRole.ACCESSED_TYPE, typeAccess.getAccessedType());
	}

	public <T> void visitCtUnaryOperator(final CtUnaryOperator<T> operator) {
		matched(operator, operator.getTypeCasts());
		scan(CtRole.ANNOTATION, operator.getAnnotations());
		scan(CtRole.TYPE, operator.getType());
		scan(CtRole.CAST, operator.getTypeCasts());
		scan(CtRole.EXPRESSION, operator.getOperand());
	}

	@Override
	public <T> void visitCtVariableRead(final CtVariableRead<T> variableRead) {
		matched(variableRead, variableRead.getTypeCasts());
		matched(variableRead, variableRead.getVariable());
		scan(CtRole.ANNOTATION, variableRead.getAnnotations());
		scan(CtRole.CAST, variableRead.getTypeCasts());
		// scan(CtRole.VARIABLE, variableRead.getVariable());
	}

	@Override
	public <T> void visitCtVariableWrite(final CtVariableWrite<T> variableWrite) {
		matched(variableWrite, variableWrite.getTypeCasts());
		matched(variableWrite, variableWrite.getVariable());
		scan(CtRole.ANNOTATION, variableWrite.getAnnotations());
		scan(CtRole.CAST, variableWrite.getTypeCasts());
		// scan(CtRole.VARIABLE, variableWrite.getVariable());
	}

	public void visitCtWhile(final CtWhile whileLoop) {
		scan(CtRole.ANNOTATION, whileLoop.getAnnotations());
		scan(CtRole.EXPRESSION, whileLoop.getLoopingExpression());
		scan(CtRole.BODY, whileLoop.getBody());
	}

	public <T> void visitCtCodeSnippetExpression(final CtCodeSnippetExpression<T> expression) {
		scan(CtRole.TYPE, expression.getType());
		scan(CtRole.ANNOTATION, expression.getAnnotations());
		scan(CtRole.CAST, expression.getTypeCasts());
	}

	public void visitCtCodeSnippetStatement(final CtCodeSnippetStatement statement) {
		scan(CtRole.ANNOTATION, statement.getAnnotations());
	}

	public <T> void visitCtUnboundVariableReference(final CtUnboundVariableReference<T> reference) {
		matched(reference, reference);
		// matched(reference,ref);
		// decl(reference);
		// TODO
		// scan(CtRole.TYPE, reference.getType());
	}

	@Override
	public <T> void visitCtFieldRead(final CtFieldRead<T> fieldRead) {
		matched(fieldRead, fieldRead.getVariable());
		matched(fieldRead, fieldRead.getTypeCasts());

		scan(CtRole.ANNOTATION, fieldRead.getAnnotations());
		scan(CtRole.CAST, fieldRead.getTypeCasts());
		CtExpression<?> target = fieldRead.getTarget();
		if (target == null || target.isImplicit() || !target.getPosition().isValidPosition()) {
			target = null;
		}
		if (target != null
				&& !target.isImplicit()
				&& (target.getPosition() == null
						|| target.getPosition() instanceof NoSourcePosition
						|| target.getPosition().isValidPosition() == false)) {
			// System.err.println(fieldRead.getPosition());
			// System.err.println(target.getPosition());
			// System.err.println(fieldRead.getVariable().getSimpleName());
			// System.err.println(fieldRead.toString());
			// System.err.println(fieldRead.getPosition().getSourceStart());
			// System.err.println(fieldRead.getPosition().getSourceEnd());
			int end = fieldRead.getPosition().getSourceEnd();
			end = end - 1 - fieldRead.getVariable().getSimpleName().length();
			SourcePosition position = new SourcePositionImpl(
					fieldRead.getPosition().getCompilationUnit(),
					fieldRead.getPosition().getSourceStart(),
					Math.max(fieldRead.getPosition().getSourceStart() + 1, end),
					((CompilationUnitImpl) fieldRead.getPosition().getCompilationUnit()).getLineSeparatorPositions());
			target.setPosition(position);
		}
		scan(CtRole.TARGET, target);
		// scan(CtRole.VARIABLE, fieldRead.getVariable());
	}

	@Override
	public <T> void visitCtFieldWrite(final CtFieldWrite<T> fieldWrite) {
		matched(fieldWrite, fieldWrite.getTypeCasts());
		matched(fieldWrite, fieldWrite.getVariable());
		scan(CtRole.ANNOTATION, fieldWrite.getAnnotations());
		scan(CtRole.CAST, fieldWrite.getTypeCasts());
		CtExpression<?> target = fieldWrite.getTarget();
		if (target != null
				&& !target.isImplicit()
				&& (target.getPosition() == null
						|| target.getPosition() instanceof NoSourcePosition
						|| target.getPosition().isValidPosition() == false)) {
			// System.err.println(fieldRead.getPosition());
			// System.err.println(target.getPosition());
			// System.err.println(fieldRead.getVariable().getSimpleName());
			// System.err.println(fieldRead.toString());
			// System.err.println(fieldRead.getPosition().getSourceStart());
			// System.err.println(fieldRead.getPosition().getSourceEnd());

			SourcePosition position = new SourcePositionImpl(
					fieldWrite.getPosition().getCompilationUnit(),
					fieldWrite.getPosition().getSourceStart(),
					fieldWrite.getPosition().getSourceEnd() - fieldWrite.getVariable().getSimpleName().length(),
					((CompilationUnitImpl) fieldWrite.getPosition().getCompilationUnit()).getLineSeparatorPositions());
			target.setPosition(position);
		}
		scan(CtRole.TARGET, target);
		// scan(CtRole.VARIABLE, fieldWrite.getVariable());
	}

	@Override
	public <T> void visitCtSuperAccess(final CtSuperAccess<T> f) {
		matched(f, f.getTypeCasts());
		matched(f, f.getVariable());
		scan(CtRole.ANNOTATION, f.getAnnotations());
		scan(CtRole.CAST, f.getTypeCasts());
		CtExpression<?> target = f.getTarget();
		if (target != null
				&& f.getTypeCasts().isEmpty()
				&& !target.isImplicit()
				&& (target.getPosition() == null
						|| target.getPosition() instanceof NoSourcePosition
						|| target.getPosition().isValidPosition() == false)) {
			// System.err.println(fieldRead.getPosition());
			// System.err.println(target.getPosition());
			// System.err.println(fieldRead.getVariable().getSimpleName());
			// System.err.println(fieldRead.toString());
			// System.err.println(fieldRead.getPosition().getSourceStart());
			// System.err.println(fieldRead.getPosition().getSourceEnd());
			SourcePosition position = new SourcePositionImpl(
					f.getPosition().getCompilationUnit(),
					f.getPosition().getSourceStart(),
					f.getPosition().getSourceEnd() - f.getVariable().getSimpleName().length(),
					((CompilationUnitImpl) f.getPosition().getCompilationUnit()).getLineSeparatorPositions());
			target.setPosition(position);
		}
		scan(CtRole.TARGET, target);
		scan(CtRole.VARIABLE, f.getVariable());
	}

	@Override
	public void visitCtComment(final CtComment comment) {
		scan(CtRole.ANNOTATION, comment.getAnnotations()); // TODO put back scanning of Comment because they can hold
															// annotations ...
	}

	@Override
	public void visitCtJavaDoc(final CtJavaDoc javaDoc) {
		// matched(javaDoc,ref);
		// decl(javaDoc);
		scan(CtRole.ANNOTATION, javaDoc.getAnnotations());
	}

	@Override
	public void visitCtJavaDocTag(final CtJavaDocTag docTag) {
		// matched(docTag,ref);
		// decl(docTag);
		scan(CtRole.ANNOTATION, docTag.getAnnotations());
	}

	@Override
	public void visitCtImport(final CtImport ctImport) {
		// matched(ctImport,ref);
		// decl(ctImport);
		scan(CtRole.IMPORT_REFERENCE, ctImport.getReference());
		scan(CtRole.ANNOTATION, ctImport.getAnnotations());
	}

	@Override
	public void visitCtModule(CtModule module) {
		// matched(module,ref);
		// decl(module);
		scan(CtRole.ANNOTATION, module.getAnnotations());
		scan(CtRole.MODULE_DIRECTIVE, module.getModuleDirectives());
		scan(CtRole.SUB_PACKAGE, module.getRootPackage());
	}

	@Override
	public void visitCtModuleReference(CtModuleReference moduleReference) {
		// matched(moduleReference,ref);
		// decl(moduleReference);
		scan(CtRole.ANNOTATION, moduleReference.getAnnotations());
	}

	@Override
	public void visitCtPackageExport(CtPackageExport moduleExport) {
		// matched(moduleExport,ref);
		// decl(moduleExport);
		scan(CtRole.PACKAGE_REF, moduleExport.getPackageReference());
		scan(CtRole.MODULE_REF, moduleExport.getTargetExport());
		scan(CtRole.ANNOTATION, moduleExport.getAnnotations());
	}

	@Override
	public void visitCtModuleRequirement(CtModuleRequirement moduleRequirement) {
		// matched(moduleRequirement,ref);
		// decl(moduleRequirement);
		scan(CtRole.MODULE_REF, moduleRequirement.getModuleReference());
		scan(CtRole.ANNOTATION, moduleRequirement.getAnnotations());
	}

	@Override
	public void visitCtProvidedService(CtProvidedService moduleProvidedService) {
		// matched(moduleProvidedService,ref);
		// decl(moduleProvidedService);
		scan(CtRole.SERVICE_TYPE, moduleProvidedService.getServiceType());
		scan(CtRole.IMPLEMENTATION_TYPE, moduleProvidedService.getImplementationTypes());
		scan(CtRole.ANNOTATION, moduleProvidedService.getAnnotations());
	}

	@Override
	public void visitCtUsedService(CtUsedService usedService) {
		// matched(usedService,ref);
		// decl(usedService);
		scan(CtRole.SERVICE_TYPE, usedService.getServiceType());
		scan(CtRole.ANNOTATION, usedService.getAnnotations());
	}

	@Override
	public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
		// matched(compilationUnit,ref);
		// decl(compilationUnit);
		scan(CtRole.ANNOTATION, compilationUnit.getAnnotations());
		scan(CtRole.PACKAGE_DECLARATION, compilationUnit.getPackageDeclaration());
		scan(CtRole.DECLARED_IMPORT, compilationUnit.getImports());
		scan(CtRole.DECLARED_MODULE_REF, compilationUnit.getDeclaredModuleReference());
		scan(CtRole.DECLARED_TYPE_REF, compilationUnit.getDeclaredTypeReferences());
	}

	@Override
	public void visitCtPackageDeclaration(CtPackageDeclaration packageDeclaration) {
		// matched(packageDeclaration,ref);
		// decl(packageDeclaration);
		scan(CtRole.ANNOTATION, packageDeclaration.getAnnotations());
		scan(CtRole.PACKAGE_REF, packageDeclaration.getReference());
	}

	@Override
	public void visitCtTypeMemberWildcardImportReference(CtTypeMemberWildcardImportReference wildcardReference) {
		// matched(wildcardReference,ref);
		// decl(wildcardReference);
		scan(CtRole.TYPE_REF, wildcardReference.getTypeReference());
	}
}
