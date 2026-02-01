package com.example.bankapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankapp.R;
import com.example.bankapp.models.Transaction;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public TransactionAdapter(List<Transaction> transactions, OnTransactionClickListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        if (transactions == null || transactions.isEmpty()) return;

        Transaction transaction = transactions.get(position);
        holder.bind(transaction);

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onTransactionClick(transaction));
        }
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView dateText;
        private final TextView amountText;
        private final TextView recipientText;
        private final TextView accountText;
        private final ImageView iconImage;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.transaction_title);
            dateText = itemView.findViewById(R.id.transaction_date);
            amountText = itemView.findViewById(R.id.transaction_amount);
            recipientText = itemView.findViewById(R.id.transaction_recipient);
            accountText = itemView.findViewById(R.id.transaction_account);
            iconImage = itemView.findViewById(R.id.transaction_icon);
        }

        public void bind(Transaction transaction) {
            if (transaction == null) return;

            setTransactionIcon(transaction);
            titleText.setText(getTransactionTitle(transaction));
            dateText.setText(formatTransactionDate(transaction.getDate()));

            amountText.setText(formatAmount(transaction.getAmount().abs().doubleValue()));
            setAmountColor(transaction.getAmount().doubleValue());

            setRecipientInfo(transaction);
            setAccountInfo(transaction);
        }

        private void setTransactionIcon(Transaction transaction) {
            double amount = transaction.getAmount().doubleValue();
            String type = transaction.getType() != null ? transaction.getType().toUpperCase() : "";

            if ("INCOME".equals(type) || amount > 0) {
                iconImage.setImageResource(R.drawable.ic_arrow_up);
                iconImage.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.success));
            } else if ("EXPENSE".equals(type) || amount < 0) {
                iconImage.setImageResource(R.drawable.ic_arrow_down);
                iconImage.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.error));
            }
        }

        private String getTransactionTitle(Transaction transaction) {
            String type = transaction.getType();
            String description = transaction.getDescription();

            if (type != null) {
                switch (type.toUpperCase()) {
                    case "EXPENSE":
                        return "Send payment";
                    case "INCOME":
                        return "Receive payment";
                }
            }

            if (description != null && !description.trim().isEmpty()) {
                return description;
            }

            double amount = transaction.getAmount().doubleValue();
            return amount > 0 ? "Receive payment" : "Send payment";
        }

        private String formatTransactionDate(String dateString) {
            if (dateString == null || dateString.isEmpty()) {
                return "No date";
            }

            try {
                SimpleDateFormat[] inputFormats = {
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                        new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()),
                        new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                };

                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

                for (SimpleDateFormat inputFormat : inputFormats) {
                    try {
                        Date date = inputFormat.parse(dateString);
                        return outputFormat.format(date);
                    } catch (ParseException e) {
                        continue;
                    }
                }
                return dateString;
            } catch (Exception e) {
                return dateString;
            }
        }

        private String formatAmount(double amount) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pl", "PL"));
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            return formatter.format(amount);
        }

        private void setAmountColor(double amount) {
            int color;
            if (amount > 0) {
                color = ContextCompat.getColor(itemView.getContext(), R.color.success);
            } else if (amount < 0) {
                color = ContextCompat.getColor(itemView.getContext(), R.color.error);
            } else {
                color = ContextCompat.getColor(itemView.getContext(), R.color.gray);
            }
            amountText.setTextColor(color);
        }

        private void setRecipientInfo(Transaction transaction) {
            if (transaction.getAccountNumber() == null || transaction.getAccountNumber().isEmpty()) {
                recipientText.setVisibility(View.GONE);
                return;
            }

            String type = transaction.getType();
            String prefix = (type != null && type.equalsIgnoreCase("EXPENSE")) ? "To: " : "From: ";
            recipientText.setText(prefix + maskAccountNumber(transaction.getAccountNumber()));
            recipientText.setVisibility(View.VISIBLE);
        }

        private void setAccountInfo(Transaction transaction) {
            if (transaction.getAccountNumber() == null || transaction.getAccountNumber().isEmpty()) {
                accountText.setVisibility(View.GONE);
                return;
            }

            accountText.setText("Account: " + maskAccountNumber(transaction.getAccountNumber()));
            accountText.setVisibility(View.VISIBLE);
        }

        private String maskAccountNumber(String accountNumber) {
            if (accountNumber.length() < 4) return "••••";
            return "••••" + accountNumber.substring(accountNumber.length() - 4);
        }
    }
}