package com.pdguard.verifier;

import java.io.FileNotFoundException;
import java.lang.String;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


import java.io.File;
import java.io.FilenameFilter;
import java.util.*;


public class Main {

	/* class Filter "returns" each java file that there is into a specific folder*/
	private static class Filter {
		private File[] finder(String dirName) {
			File dir = new File(dirName);
			return dir.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String filename) {
					return filename.endsWith(".java");
					}
					});
		}
	}

	public static void main(String[] args) throws Exception {
		// creates an input stream for the file to be parsed
		File[] fil = new Filter().finder("/home/nikos/PDGuard/apps/eshop/app/forms");
		Vector<String> varsToCheck = new Vector<String>(); // varsToCheck and varsToCheck2 keep the same content used in various fields
		Vector<String> varsToCheck2 = new Vector<String>();
		varsToCheck.add("decryptData");
		varsToCheck.add("encryptData");
		System.out.println("=======================");
		Vector<CompilationUnit> compilationUnits = new Vector<CompilationUnit>();
		Vector<String> inValidWrappers = new Vector<String>();

		try{
			for (File fi : fil) compilationUnits.add(JavaParser.parse(fi));
			fil = new Filter().finder("/home/nikos/bla/test");
			for (File fi : fil) compilationUnits.add(JavaParser.parse(fi));
		} catch (FileNotFoundException e){}

		for (CompilationUnit cu : compilationUnits) new fin().visit(cu, varsToCheck,inValidWrappers);
		varsToCheck2.addAll(varsToCheck);
		varsToCheck2 = cutVector(varsToCheck2);	// deletes duplicate elements
		varsToCheck = cutVector(varsToCheck);
		for (CompilationUnit cu : compilationUnits)
			for (String s : varsToCheck)
				new fin().visit(cu, varsToCheck2,inValidWrappers);

		System.out.println("=======================");
	}

	private static class isValid extends VoidVisitorAdapter<String[]> {
		@Override
			public void visit(MethodCallExpr n,String[] arg) {
				NodeList<Expression> nodeList = ((MethodCallExpr) n).getArguments();
				String argument;
				for (Expression ex : nodeList) {
					if (ex instanceof MethodCallExpr) argument = ((MethodCallExpr) ex).getNameAsString();
					else argument = ex.toString();
					if ((((MethodCallExpr) n).getNameAsString().equals("write") || n.getNameAsString().equals(arg[0])) && argument.equals(arg[1]))
						System.out.println("WARNING: Data written to file!");
					else if ((((MethodCallExpr) n).getNameAsString().equals("setString") || ((MethodCallExpr) n).getNameAsString().equals("setInt")) && argument.equals(arg))
						System.out.println("WARNING: Connected to Database");

				}
			}
	}

	private static class fin {

		private void visit(CompilationUnit cu, Vector<String> varsToCheck,Vector<String> inValidWrappers) {
			MethodDeclaration method;
			BlockStmt blocks;
			Vector<String> tempVars = new Vector<String>();
			NodeList<TypeDeclaration<?>> types = cu.getTypes();
			for (TypeDeclaration<?> type : types) {
				// Go through all fields, methods, etc. in this type
				NodeList<BodyDeclaration<?>> members = type.getMembers();
				for (BodyDeclaration<?> member : members) {
					if (member instanceof MethodDeclaration) {	//for each method
						method = (MethodDeclaration) member;
						tempVars.clear();
						Vector<String> temp = new Vector<String>();
						temp.addAll(tempVars);
						temp.addAll(varsToCheck);
						try{
							blocks = method.getBody().get();
						} catch (NoSuchElementException w) { continue; }
						NodeList<Statement> nodes = blocks.getStatements();
						for (Statement st : nodes) { // fpr each statement of a method
							temp.addAll(tempVars);
							temp = cutVector(temp);
							if (st instanceof ReturnStmt) {
								Expression ex = ((ReturnStmt) st).getExpression().get();
								if (ex instanceof MethodCallExpr)
									forMethodCallonRetStmt((MethodCallExpr) ex, varsToCheck, tempVars, method.getNameAsString(),inValidWrappers);
								else if (ex instanceof ObjectCreationExpr)
									forObjCreation((ObjectCreationExpr)ex,varsToCheck,tempVars,((MethodDeclaration) member).getNameAsString());
							}
							else if (st instanceof ExpressionStmt) {
								Expression ex = ((ExpressionStmt) st).getExpression();
								if (ex instanceof VariableDeclarationExpr)
									forVarDec((VariableDeclarationExpr) ex, varsToCheck, tempVars);
								else if (ex instanceof AssignExpr)
									forAssign((AssignExpr) ex, varsToCheck, tempVars,inValidWrappers);
								else if (ex instanceof MethodCallExpr)
									forMethodCall((MethodCallExpr) ex, varsToCheck, tempVars,method.getNameAsString(),inValidWrappers);
							}
							else if(st instanceof TryStmt){
								BlockStmt blockStmt = ((TryStmt) st).getTryBlock().get();
								NodeList<Statement> nodeList = blockStmt.getStatements();
								for(Statement statement : nodeList){
									if (statement instanceof ReturnStmt) {
										Expression ex = ((ReturnStmt) statement).getExpression().get();
										if (ex instanceof MethodCallExpr)
											forMethodCallonRetStmt((MethodCallExpr) ex, varsToCheck, tempVars, method.getNameAsString(),inValidWrappers);
										else if (ex instanceof ObjectCreationExpr)
											forObjCreation((ObjectCreationExpr)ex,varsToCheck,tempVars,((MethodDeclaration) member).getNameAsString());
									}
									else if (statement instanceof ExpressionStmt) {
										try {
											Expression ex = ((ExpressionStmt) st).getExpression();

											if (ex instanceof VariableDeclarationExpr)
												forVarDec((VariableDeclarationExpr) ex, varsToCheck, tempVars);
											else if (ex instanceof AssignExpr)
												forAssign((AssignExpr) ex, varsToCheck, tempVars,inValidWrappers);
											else if (ex instanceof MethodCallExpr)
												forMethodCall((MethodCallExpr) ex, varsToCheck, tempVars,method.getNameAsString(),inValidWrappers);
										} catch (ClassCastException c) {continue;}
									}
								}

							}
						}
					}

				}
			}
			varsToCheck.addAll(tempVars);
			varsToCheck = cutVector(varsToCheck);
		}


		private void forAssign(AssignExpr ex, Vector<String> varsToCheck, Vector<String> tempVars,Vector<String> inValidWrappers) {
			Vector<String> temp = new Vector<String>();
			temp.addAll(tempVars);
			temp.addAll(varsToCheck);
			Expression target = ((AssignExpr) ex).getTarget();
			Expression value = ((AssignExpr) ex).getValue();
			if (value instanceof MethodCallExpr) {
				for (String s : temp) {
					if (((MethodCallExpr) value).getNameAsString().equals(s)) tempVars.add(target.toString());
					else {
						NodeList<Expression> nodelist = ((MethodCallExpr) value).getArguments();
						for (Expression argument : nodelist) {
							if (argument.toString().equals(s))
								check(temp,(MethodCallExpr)value,inValidWrappers);
						}
					}
				}
			} else if (value instanceof ObjectCreationExpr) {
				NodeList<Expression> nodeList = ((ObjectCreationExpr) value).getArguments();
				for (Expression expression : nodeList) {
					for (String s : temp) {
						if (expression instanceof MethodCallExpr)
							if (((MethodCallExpr) expression).getNameAsString().equals(s)) {
								tempVars.add(target.toString());
								tempVars = cutVector(tempVars);
							}
						if (expression.toString().equals(s)) {
							tempVars.add(target.toString());
							tempVars = cutVector(tempVars);
						}
					}
				}
			}
			else if(value instanceof BinaryExpr){
				for(String s : temp){
					if( ((BinaryExpr) value).getLeft().toString().equals(s) || ((BinaryExpr) value).getRight().toString().equals(s))
						tempVars.add(target.toString());

				}
			}
			else
				for(String s:temp)
					if(value.toString().equals(s)) tempVars.add(s);
		}

		private void forVarDec(VariableDeclarationExpr ex, Vector<String> varsToCheck, Vector<String> tempVars) {
			Vector<String> temp = new Vector<String>();
			temp.addAll(tempVars);
			temp.addAll(varsToCheck);
			NodeList<VariableDeclarator> nlvd = ((VariableDeclarationExpr) ex).getVariables();
			for (VariableDeclarator vardec : nlvd) {
				for (String s : tempVars) if (!temp.contains(s)) temp.add(s);
				for (String s : temp) {
					try{if (vardec.getInitializer().get() instanceof MethodCallExpr) {
						for (String s3 : varsToCheck)
							if (((MethodCallExpr) vardec.getInitializer().get()).getNameAsString().equals(s3))
								tempVars.add(vardec.getNameAsString());
						//else new isValid().visit((MethodCallExpr) vardec.getInitializer().get(), s);
					}
							else if(vardec.getInitializer().get() instanceof BinaryExpr)
								if((((BinaryExpr) vardec.getInitializer().get()).getRight().toString().equals(s) ||(((BinaryExpr) vardec.getInitializer().get()).getLeft().toString().equals(s)))) tempVars.add(vardec.getNameAsString());

								else if (vardec.getInitializer().get().toString().equals(s)) {
									tempVars.add(vardec.getInitializer().get().toString());
								}
					} catch (NoSuchElementException n) { continue;}
				}
			}
		}

		private void forMethodCall(MethodCallExpr ex, Vector<String> varsToCheck, Vector<String> tempVars,String methodName,Vector<String> inValidWrappers) {
			Vector<String> temp = new Vector<String>();
			temp.addAll(varsToCheck);
			temp.addAll(tempVars);
			NodeList<Expression> nodeList = ex.getArguments();
			if(ex.getNameAsString().equals("write")) inValidWrappers.add(methodName);
			for (String s : temp) {
				for (Expression expr : nodeList)
					if (expr instanceof MethodCallExpr) {
						NodeList<Expression> nodeList1 = ((MethodCallExpr) expr).getArguments();
						for (Expression expression : nodeList1)
							if (expression.toString().equals(s)) {
								check(temp,(MethodCallExpr)expr,inValidWrappers);
							}
					}
			}
			check(temp,ex,inValidWrappers);
		}

		private void check(Vector<String> temp,MethodCallExpr ex,Vector<String> inValidWrappers){
			String[] array = new String[2];
			temp = cutVector(temp);
			inValidWrappers =  cutVector(inValidWrappers);
			for (String s : temp) {
				for (String wrapper : inValidWrappers) {
					array[0] = wrapper;
					array[1] = s;
					new isValid().visit(ex, array);
				}
			}

		}
		private void forMethodCallonRetStmt(MethodCallExpr ex, Vector<String> varsToCheck, Vector<String> tempVars, String curMethod,Vector<String> inValidWrappers) {
			Vector<String> temp = new Vector<String>();
			temp.addAll(varsToCheck);
			temp.addAll(tempVars);
			NodeList<Expression> nodeList = ex.getArguments();
			if(ex.getNameAsString().equals("write")) inValidWrappers.add(curMethod);
			for (String s : temp) {
				if (ex.getNameAsString().equals(s)) varsToCheck.add(curMethod);
				for (Expression expr : nodeList)
					if (expr instanceof MethodCallExpr) {
						if (((MethodCallExpr) expr).getNameAsString().equals(s))
							varsToCheck.add(curMethod);
						NodeList<Expression> nodeList1 = ((MethodCallExpr) expr).getArguments();
						for (Expression expression : nodeList1) {
							if (expression instanceof MethodCallExpr)
								check(temp,ex,inValidWrappers);
							if (expression.toString().equals(s)) {
								if (expression.toString().equals(s))
									varsToCheck.add(curMethod);
							}
						}
					}
			}

		}
		private void forObjCreation(ObjectCreationExpr ex,Vector<String> varsToCheck,Vector<String> tempVars,String curMethod){
			Vector<String> temp = new Vector<String>();
			temp.addAll(varsToCheck);
			temp.addAll(tempVars);
			NodeList<Expression> nodeList = ((ObjectCreationExpr) ex).getArguments();
			for (Expression expr : nodeList) {
				for (String s : temp)
					if (expr.toString().equals(s)) {
						varsToCheck.add(curMethod);
					}
				if (expr instanceof MethodCallExpr) {
					for (String s : temp)
						if (((MethodCallExpr) expr).getNameAsString().equals(s))
							varsToCheck.add(curMethod);

				}
			}
		}
	}

	private static Vector<String> cutVector(Vector<String> v) {
		Vector<String> temp = new Vector<String>();
		for (String s : v) if (!temp.contains(s)) temp.add(s);
		return temp;
	}
}
