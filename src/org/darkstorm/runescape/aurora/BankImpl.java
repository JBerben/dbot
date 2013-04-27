package org.darkstorm.runescape.aurora;

import org.darkstorm.runescape.api.Bank;
import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.Item;

public final class BankImpl extends AbstractUtility implements Bank {

	public BankImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public Item getItem(Filter<Item> filter) {
		return null;
	}

	@Override
	public Item[] getItems(Filter<Item> filter) {
		return null;
	}

	@Override
	public Item[] getItems() {
		return null;
	}

	@Override
	public void withdrawAll(Item item) {
	}

	@Override
	public void withdrawAllButOne(Item item) {
	}

	@Override
	public void withdrawX(Item item, int amount) {
	}

	@Override
	public void scrollTo(Item item) {
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public void openNearest() {
	}

	@Override
	public void walkToNearest() {
	}

	@Override
	public void deposit(Filter<Item> filter) {
	}

	@Override
	public void depositShortcut(DepositShortcut shortcut) {
	}

	@Override
	public boolean isNoted() {
		return false;
	}

	@Override
	public void setNoted(boolean noted) {
	}

	@Override
	public void withdraw(Filter<Item> filter) {
	}

	@Override
	public void withdrawAll(Filter<Item> filter) {
	}

	@Override
	public void withdraw(Item item) {
	}

	@Override
	public void depositAll(Filter<Item> filter) {
	}

	@Override
	public void deposit(Item item) {
	}

	@Override
	public void depositAll(Item item) {
	}

	@Override
	public void depositAllButOne(Item item) {
	}

	@Override
	public void depositX(Item item, int amount) {
	}

}
