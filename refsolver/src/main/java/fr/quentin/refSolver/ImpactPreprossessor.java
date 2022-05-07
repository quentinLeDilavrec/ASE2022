package fr.quentin.refSolver;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayAccess;
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
import spoon.reflect.code.CtFieldAccess;
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
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
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
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.CtVisitable;

/**
 * Mine relations from declarations to references.
 */
final class ImpactPreprossessor extends CtScanner {

    private List<CtExecutableReference<?>> allMethodsReferences;

    public ImpactPreprossessor(List<CtExecutableReference<?>> allMethodsReferences) {
        this.allMethodsReferences = allMethodsReferences;
    }

    private <T> void insertMetaData(final CtElement element, final String key, final Collection<T> defaultValue,
            final T value) {
        Object x = element.getMetadata(key);
        if (x == null) {
            x = defaultValue;
            element.putMetadata(key, x);
        }
        if (x instanceof Collection) {
            ((Collection<T>) x).add(value);
        }
    }

    private void counterMetaData(final CtElement e, final String key) {
        Object x = e.getMetadata(key);
        if (x == null) {
            x = new Integer(0);
            e.putMetadata(key, x);
        }
        if (x instanceof Integer) {
            Integer y = (Integer) x;
            y += 1;
        }
    }

    public <T> void visitCtAbstractInvocation(final CtAbstractInvocation<T> executable) {
        final CtExecutableReference<T> a = executable.getExecutable();

        final CtExecutable<T> b = a.getDeclaration();
        if (b != null) {
            insertMetaData(b, "call", new HashSet<>(), executable);
        }
    }

    @Override
    public <T> void visitCtInvocation(final CtInvocation<T> invocation) {
        super.visitCtInvocation(invocation);
        visitCtAbstractInvocation(invocation);
    }

    @Override
    public <T> void visitCtConstructorCall(final CtConstructorCall<T> constructor) {
        super.visitCtConstructorCall(constructor);
        visitCtAbstractInvocation(constructor);
    }

    // TODO take inspiration from gumtree-spoon

    // @Override
    // public <A extends Annotation> void visitCtAnnotation(final CtAnnotation<A>
    // annotation) {
    // super.visitCtAnnotation(annotation);
    // annotation.getAnnotationType();
    // annotation.getType();
    // annotation.getTypeCasts();
    // }

    // @Override
    // public <T> void visitCtAnnotationFieldAccess(final CtAnnotationFieldAccess<T>
    // annotationFieldAccess) {
    // super.visitCtAnnotationFieldAccess(annotationFieldAccess);
    // annotationFieldAccess.getVariable();
    // annotationFieldAccess.getType();
    // annotationFieldAccess.getTypeCasts();
    // }

    @Override
    public <T> void visitCtCatchVariable(final CtCatchVariable<T> catchVariable) {
        super.visitCtCatchVariable(catchVariable);
        // catchVariable.getReference();
        // catchVariable.getType();
        // catchVariable.getMultiTypes();
        visitCtVariable(catchVariable);
    }

    @Override
    public <T> void visitCtClass(final CtClass<T> ctClass) {
        super.visitCtClass(ctClass);
        // ctClass.getUsedTypes(true);
        // ctClass.getUsedTypes(false);
        // ctClass.getReference();

        final Set<CtTypeReference<?>> a = ctClass.getUsedTypes(false);

        for (CtTypeReference<?> b : a) {
            final CtType<?> c = b.getDeclaration();
            if (c != null) {
                insertMetaData(c, "type", new HashSet<>(), ctClass);
            }
        }
        // TODO is there more? (not sure)
    }

    // @Override
    // public void visitCtCompilationUnit(final CtCompilationUnit compilationUnit) {
    // super.visitCtCompilationUnit(compilationUnit);
    // compilationUnit.getDeclaredTypeReferences();
    // compilationUnit.getDeclaredModuleReference();
    // }

    // @Override
    // public <T> void visitCtConstructor(final CtConstructor<T> c) {
    // super.visitCtConstructor(c);
    // c.getReference();
    // c.getReferencedTypes();
    // }

    // @Override
    // public <T> void visitCtEnumValue(final CtEnumValue<T> enumValue) {
    // super.visitCtEnumValue(enumValue);
    // enumValue.getReference();
    // }

    // @Override
    // public <T, E extends CtExpression<?>> void
    // visitCtExecutableReferenceExpression(
    // final CtExecutableReferenceExpression<T, E> expression) {
    // super.visitCtExecutableReferenceExpression(expression);
    // expression.getExecutable();
    // expression.getType();
    // expression.getTypeCasts();
    // }

    @Override
    public <T> void visitCtField(final CtField<T> f) {// inherit CtNamedElement and CtTypedElement
        super.visitCtField(f);
        // f.getReference();
        // f.getType();

        visitCtVariable(f);
    }

    @Override
    public <T> void visitCtFieldRead(final CtFieldRead<T> fieldRead) {
        super.visitCtFieldRead(fieldRead);
        // fieldRead.getType();
        // fieldRead.getVariable();
        // fieldRead.getTypeCasts();
        visitCtFieldAccess(fieldRead);
    }

    @Override
    public <T> void visitCtFieldWrite(final CtFieldWrite<T> fieldWrite) {
        super.visitCtFieldWrite(fieldWrite);
        // fieldWrite.getType();
        // fieldWrite.getVariable();
        // fieldWrite.getTypeCasts();
        visitCtFieldAccess(fieldWrite);
    }

    public <T> void visitCtFieldAccess(final CtFieldAccess<T> field) {
        CtFieldReference<T> a = field.getVariable();

        final CtField<T> b = a.getDeclaration();
        if (b != null) {
            insertMetaData(b, "variable", new HashSet<>(), field);
        }
    }

    // @Override
    // public void visitCtImport(final CtImport ctImport) {
    // super.visitCtImport(ctImport);
    // ctImport.getReference();
    // }

    // @Override
    // public <T> void visitCtInterface(final CtInterface<T> intrface) {
    // super.visitCtInterface(intrface);
    // intrface.getReference();
    // }

    // @Override
    // public <T> void visitCtLambda(final CtLambda<T> lambda) {
    // super.visitCtLambda(lambda);
    // // lambda.getReference();
    // // lambda.getType();
    // // lambda.getTypeCasts();
    // }

    @Override
    public <T> void visitCtLiteral(final CtLiteral<T> literal) {
        super.visitCtLiteral(literal);
        // literal.getType();
        // literal.getTypeCasts();

        CtTypeReference<T> a = literal.getType();

        final CtType<T> b = a.getDeclaration();
        if (b != null) {
            insertMetaData(b, "type", new HashSet<>(), literal);
        }
    }

    @Override
    public <T> void visitCtLocalVariable(final CtLocalVariable<T> localVariable) {
        super.visitCtLocalVariable(localVariable);
        // localVariable.getReference();
        // localVariable.getType();

        visitCtVariable(localVariable);
    }

    public <T> void visitCtVariable(final CtVariable<T> variable) {
        // localVariable.getReference();
        // localVariable.getType();

        CtTypeReference<T> a = variable.getType();

        final CtType<T> b = a.getDeclaration();
        if (b != null) {
            insertMetaData(b, "type", new HashSet<>(), variable);
        }
    }

    // @Override
    // public <T> void visitCtMethod(final CtMethod<T> m) {
    // super.visitCtMethod(m);
    // // m.getReference();
    // // m.getType(); // return type
    // // m.getThrownTypes();
    // }

    // @Override
    // public void visitCtModule(final CtModule module) {
    // super.visitCtModule(module);
    // module.getReference();
    // }

    // @Override
    // public void visitCtModuleRequirement(final CtModuleRequirement
    // moduleRequirement) {
    // super.visitCtModuleRequirement(moduleRequirement);
    // moduleRequirement.getModuleReference();
    // }

    @Override
    public <T> void visitCtNewArray(final CtNewArray<T> newArray) {
        super.visitCtNewArray(newArray);
        // newArray.getType();
        // newArray.getTypeCasts();

        CtTypeReference<T> a = newArray.getType();

        final CtType<T> b = a.getDeclaration();
        if (b != null) {
            insertMetaData(b, "type", new HashSet<>(), newArray);
        }
    }

    // @Override
    // public <T> void visitCtNewClass(final CtNewClass<T> newClass) { // already
    // handled by CtConstructorCall
    // super.visitCtNewClass(newClass);
    // newClass.getType();
    // // newClass.getTypeCasts();
    // newClass.getExecutable();
    // newClass.getActualTypeArguments();

    // CtTypeReference<T> a = newClass.getType();

    // final CtType<T> b = a.getDeclaration();
    // if (b != null) {
    // insertMetaData(b, "type", new HashSet<>(), newClass);
    // }

    // CtTypeReference<T> c = newClass.getType();

    // final CtType<T> d = c.getDeclaration();
    // if (d != null) {
    // insertMetaData(d, "call", new HashSet<>(), newClass);
    // }
    // }

    // @Override
    // public <T, A extends T> void visitCtOperatorAssignment(final
    // CtOperatorAssignment<T, A> assignment) {
    // super.visitCtOperatorAssignment(assignment);
    // assignment.getType();
    // assignment.getTypeCasts();
    // }

    // @Override
    // public void visitCtPackage(final CtPackage ctPackage) {
    // // super.visitCtPackage(ctPackage);
    // ctPackage.getReference();
    // }

    // @Override
    // public void visitCtPackageDeclaration(final CtPackageDeclaration
    // packageDeclaration) {
    // super.visitCtPackageDeclaration(packageDeclaration);
    // packageDeclaration.getReference();
    // }

    // @Override
    // public void visitCtPackageExport(final CtPackageExport moduleExport) {
    // super.visitCtPackageExport(moduleExport);
    // moduleExport.getPackageReference();
    // moduleExport.getTargetExport();
    // }

    @Override
    public <T> void visitCtParameter(final CtParameter<T> parameter) { // CtVariableReference
        super.visitCtParameter(parameter);
        // parameter.getType();

        CtTypeReference<T> a = parameter.getType();

        final CtType<T> b = a.getDeclaration();
        if (b != null) {
            insertMetaData(b, "type", new HashSet<>(), parameter);
        }
    }

    // @Override
    // public void visitCtProvidedService(final CtProvidedService
    // moduleProvidedService) {
    // super.visitCtProvidedService(moduleProvidedService);
    // moduleProvidedService.getServiceType();
    // moduleProvidedService.getImplementationTypes();
    // }

    // @Override
    // public <T> void visitCtSuperAccess(final CtSuperAccess<T> f) {
    // super.visitCtSuperAccess(f);
    // // f.getVariable();
    // // f.getTypeCasts();
    // // f.getType();
    // }

    @Override
    public <T> void visitCtTypeAccess(final CtTypeAccess<T> typeAccess) {
        super.visitCtTypeAccess(typeAccess);
        // typeAccess.getType();
        // typeAccess.getAccessedType();
        // typeAccess.getTypeCasts();
        CtTypeReference<T> a = typeAccess.getAccessedType();

        final CtType<T> b = a.getDeclaration();
        if (b != null) {
            insertMetaData(b, "type", new HashSet<>(), typeAccess);
        }
    }

    // @Override
    // public void visitCtTypeParameter(final CtTypeParameter typeParameter) {
    // super.visitCtTypeParameter(typeParameter);
    // typeParameter.getReference();
    // typeParameter.getSuperclass();
    // }

    // @Override
    // public <T> void visitCtUnaryOperator(final CtUnaryOperator<T> operator) {
    // super.visitCtUnaryOperator(operator);
    // operator.getType();
    // operator.getTypeCasts();
    // }

    // @Override
    // public void visitCtUsedService(final CtUsedService usedService) {
    // super.visitCtUsedService(usedService);
    // usedService.getServiceType();
    // }

    @Override
    public <T> void visitCtVariableRead(final CtVariableRead<T> variableRead) {
        super.visitCtVariableRead(variableRead);
        // variableRead.getType();
        // variableRead.getVariable();
        // variableRead.getTypeCasts();

        visitCtVariableAccess(variableRead);
    }

    @Override
    public <T> void visitCtVariableWrite(final CtVariableWrite<T> variableWrite) {
        super.visitCtVariableWrite(variableWrite);
        // variableWrite.getType();
        // variableWrite.getVariable();
        // variableWrite.getTypeCasts();

        visitCtVariableAccess(variableWrite);
    }

    public <T> void visitCtVariableAccess(final CtVariableAccess<T> variableAccess) {
        CtVariableReference<T> a = variableAccess.getVariable();

        final CtVariable<T> b = a.getDeclaration();
        if (b != null) {
            insertMetaData(b, "variable", new HashSet<>(), variableAccess);
        }
    }

    // MORE direct refs access not found

    // @Override
    // public <T> void visitCtAnnotationMethod(final CtAnnotationMethod<T>
    // annotationMethod) {
    // super.visitCtAnnotationMethod(annotationMethod);
    // annotationMethod.getClass();
    // }

    // @Override
    // public <A extends Annotation> void visitCtAnnotationType(final
    // CtAnnotationType<A> annotationType) {
    // super.visitCtAnnotationType(annotationType);
    // annotationType.getClass();
    // }

    // @Override
    // public void visitCtAnonymousExecutable(final CtAnonymousExecutable
    // anonymousExec) {
    // super.visitCtAnonymousExecutable(anonymousExec);
    // anonymousExec.getClass();
    // }

    @Override
    public <T> void visitCtArrayRead(final CtArrayRead<T> arrayRead) {
        super.visitCtArrayRead(arrayRead);
        CtExpression<?> a = arrayRead.getTarget();
        if (a instanceof CtVisitable) {
            ((CtVisitable) a).accept(this);
        } else if (a instanceof CtVariableAccess) {
            visitCtVariableAccess((CtVariableAccess<?>) a);
        }
    }

    @Override
    public <T> void visitCtArrayWrite(final CtArrayWrite<T> arrayWrite) {
        super.visitCtArrayWrite(arrayWrite);
        CtExpression<?> a = arrayWrite.getTarget();
        if (a instanceof CtVisitable) {
            ((CtVisitable) a).accept(this);
        } else if (a instanceof CtExpression) {
            visitCtExpression((CtExpression<?>) a);
        }
    }

    public <T> void visitCtExpression(final CtExpression<T> expression) {
        if (expression instanceof CtVisitable) {
            ((CtVisitable) expression).accept(this);
        } else if (expression instanceof CtVariableAccess) {
            visitCtVariableAccess((CtVariableAccess<?>) expression);
        } else if (expression instanceof CtFieldAccess) {
            visitCtFieldAccess((CtFieldAccess<?>) expression);
        } else if (expression instanceof CtBinaryOperator) {
            visitCtBinaryOperator((CtBinaryOperator<?>) expression);
        } else if (expression instanceof CtUnaryOperator) {
            visitCtUnaryOperator((CtUnaryOperator<?>) expression);
        } else if (expression instanceof CtArrayAccess) {
            visitCtArrayAccess((CtArrayAccess<?,?>) expression);
        }
    }

    public <T,E extends CtExpression<?>> void visitCtArrayAccess(final CtArrayAccess<T,E> array) {
        CtExpression<?> a = array.getTarget();
        if (a instanceof CtVisitable) {
            ((CtVisitable) a).accept(this);
        } else if (a instanceof CtExpression) {
            visitCtExpression((CtExpression<?>) a);
        }
    }

    @Override
    public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
        super.visitCtBinaryOperator(operator);
        visitCtExpression(operator.getLeftHandOperand());
        visitCtExpression(operator.getRightHandOperand());
    }

    @Override
    public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
        super.visitCtUnaryOperator(operator);
        visitCtExpression(operator.getOperand());
    }

    // @Override
    // public <T> void visitCtAssert(final CtAssert<T> asserted) {
    // super.visitCtAssert(asserted);
    // asserted.getClass();
    // }

    // @Override
    // public <T, A extends T> void visitCtAssignment(final CtAssignment<T, A>
    // assignement) {
    // super.visitCtAssignment(assignement);
    // assignement.getClass();
    // }

    // @Override
    // public <T> void visitCtBinaryOperator(final CtBinaryOperator<T> operator) {
    // super.visitCtBinaryOperator(operator);
    // operator.getClass();
    // }

    // @Override
    // public <R> void visitCtBlock(final CtBlock<R> block) {
    // super.visitCtBlock(block);
    // block.getClass();
    // }

    // @Override
    // public void visitCtBreak(final CtBreak breakStatement) {
    // super.visitCtBreak(breakStatement);
    // breakStatement.getClass();
    // }

    // @Override
    // public <S> void visitCtCase(final CtCase<S> caseStatement) {
    // super.visitCtCase(caseStatement);
    // caseStatement.getClass();
    // }

    // @Override
    // public void visitCtCatch(final CtCatch catchBlock) {
    // super.visitCtCatch(catchBlock);
    // catchBlock.getClass();
    // }

    // @Override
    // public <T> void visitCtCodeSnippetExpression(final CtCodeSnippetExpression<T>
    // expression) {
    // super.visitCtCodeSnippetExpression(expression);
    // expression.getClass();
    // }

    // @Override
    // public void visitCtCodeSnippetStatement(final CtCodeSnippetStatement
    // statement) {
    // super.visitCtCodeSnippetStatement(statement);
    // statement.getClass();
    // }

    // @Override
    // public void visitCtComment(final CtComment comment) {
    // super.visitCtComment(comment);
    // comment.getClass();
    // }

    // @Override
    // public <T> void visitCtConditional(final CtConditional<T> conditional) {
    // super.visitCtConditional(conditional);
    // conditional.getClass();
    // }

    // @Override
    // public void visitCtContinue(final CtContinue continueStatement) {
    // super.visitCtContinue(continueStatement);
    // continueStatement.getClass();
    // }

    // @Override
    // public void visitCtDo(final CtDo doLoop) {
    // super.visitCtDo(doLoop);
    // doLoop.getClass();
    // }

    // @Override
    // public <T extends Enum<?>> void visitCtEnum(final CtEnum<T> ctEnum) { //
    // extends CtClass
    // super.visitCtEnum(ctEnum);
    // ctEnum.getClass();
    // }
    // @Override
    // public void visitCtFor(final CtFor forLoop) {
    // super.visitCtFor(forLoop);
    // forLoop.getClass();
    // }

    // @Override
    // public void visitCtForEach(final CtForEach foreach) {
    // super.visitCtForEach(foreach);
    // foreach.getClass();
    // }

    // @Override
    // public void visitCtIf(final CtIf ifElement) {
    // super.visitCtIf(ifElement);
    // ifElement.getClass();
    // }

    // @Override
    // public <R> void visitCtReturn(final CtReturn<R> returnStatement) {
    // super.visitCtReturn(returnStatement);
    // }

    // @Override
    // public <R> void visitCtStatementList(final CtStatementList statements) {
    // super.visitCtStatementList(statements);
    // }

    // @Override
    // public void visitCtJavaDoc(final CtJavaDoc javaDoc) {
    // super.visitCtJavaDoc(javaDoc);
    // }

    // @Override
    // public void visitCtJavaDocTag(final CtJavaDocTag docTag) {
    // super.visitCtJavaDocTag(docTag);
    // docTag.getClass();
    // }

    // @Override
    // public <S> void visitCtSwitch(final CtSwitch<S> switchStatement) {
    // super.visitCtSwitch(switchStatement);
    // }

    // @Override
    // public void visitCtSynchronized(final CtSynchronized synchro) {
    // super.visitCtSynchronized(synchro);
    // }

    // @Override
    // public <T> void visitCtThisAccess(final CtThisAccess<T> thisAccess) {
    // super.visitCtThisAccess(thisAccess);
    // thisAccess.getType();
    // thisAccess.getTypeCasts();
    // }

    // @Override
    // public void visitCtThrow(final CtThrow throwStatement) {
    // super.visitCtThrow(throwStatement);
    // }

    // @Override
    // public void visitCtTry(final CtTry tryBlock) {
    // super.visitCtTry(tryBlock);
    // }

    // @Override
    // public void visitCtTryWithResource(final CtTryWithResource tryWithResource) {
    // super.visitCtTryWithResource(tryWithResource);
    // }

    // @Override
    // public void visitCtWhile(final CtWhile whileLoop) {
    // super.visitCtWhile(whileLoop);
    // }
}