package com.example.androidcw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import entities.Expense;

public class ExpenseDetails extends AppCompatActivity {
    Intent intent;

    DBHelper db;

    @Override
    protected void onResume() {
        super.onResume();

        intent = getIntent();
        db = new DBHelper(this);

        // Get the expense item send from the expense list view
        Expense expenseItem = (Expense) intent.getSerializableExtra("expenseItem");
        // Refetch so the information can be updated
        Expense expense = db.getExpense(expenseItem.getId(), expenseItem.getId());

        TextView expenseType = findViewById(R.id.textExpenseType);
        expenseType.setText(expense.getType());

        TextView expenseDate = findViewById(R.id.textExpenseDate);
        expenseDate.setText(expense.getDate());

        TextView expenseAmount = findViewById(R.id.textExpenseAmount);
        expenseAmount.setText(expense.getAmount());

        TextView expenseComments = findViewById(R.id.textExpenseComment);
        expenseComments.setText(expense.getComments());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        intent = getIntent();
        db = new DBHelper(this);

        // Get the expense item send from the expense list view
        Expense expenseItem = (Expense) intent.getSerializableExtra("expenseItem");

        TextView expenseType = findViewById(R.id.textExpenseType);
        expenseType.setText(expenseItem.getType());

        TextView expenseDate = findViewById(R.id.textExpenseDate);
        expenseDate.setText(expenseItem.getDate());

        TextView expenseAmount = findViewById(R.id.textExpenseAmount);
        expenseAmount.setText(expenseItem.getAmount());

        TextView expenseComments = findViewById(R.id.textExpenseComment);
        expenseComments.setText(expenseItem.getComments());

        Button btnBack = findViewById(R.id.btnBack4);
        btnBack.setOnClickListener(view -> this.finish());

        Button btnEdit = findViewById(R.id.btnEditExpense);
        btnEdit.setOnClickListener(view ->
            startActivity(
                    new Intent(this, AddExpense.class)
                            .putExtra("expenseItem", expenseItem)
            )
        );

        Button btnDelete = findViewById(R.id.btnDeleteExpense);
        btnDelete.setOnClickListener(view -> {
            db.deleteExpense(expenseItem.getId());
            this.finish();
        });

    }
}