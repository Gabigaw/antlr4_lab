package compiler;

import grammar.firstBaseVisitor;
import grammar.firstParser;

public class CanOptimizeVisitor extends firstBaseVisitor<Integer> {

    @Override
    public Integer visitIntExpr(firstParser.IntExprContext ctx) {
        return Integer.valueOf(ctx.INT().getText());
    }

    @Override
    public Integer visitParensExpr(firstParser.ParensExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Integer visitAddExpr(firstParser.AddExprContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));
        String op = ctx.getChild(1).getText();

        if (op.equals("+")) {
            return left + right;
        } else {
            return left - right;
        }
    }

    @Override
    public Integer visitMulExpr(firstParser.MulExprContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));
        String op = ctx.getChild(1).getText();

        if (op.equals("*")) {
            return left * right;
        } else {
            if (right == 0) {
                throw new ArithmeticException("Div by zero");
            }
            return left / right;
        }
    }
}