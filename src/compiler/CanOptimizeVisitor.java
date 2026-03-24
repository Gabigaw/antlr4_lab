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
        int left = visit(ctx.expr(0));
        int right = visit(ctx.expr(1));

        if (ctx.ADD() != null) {
            return left + right;
        } else {
            return left - right;
        }
    }

    @Override
    public Integer visitMulExpr(firstParser.MulExprContext ctx) {
        int left = visit(ctx.expr(0));
        int right = visit(ctx.expr(1));

        if (ctx.MUL() != null) {
            return left * right;
        } else {
            if (right == 0) {
                throw new ArithmeticException("Div by zero");
            }
            return left / right;
        }
    }
}