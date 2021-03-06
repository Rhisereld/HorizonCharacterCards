package com.gmail.Rhisereld.HorizonCharacterCards;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.gmail.Rhisereld.HorizonProfessions.ProfessionAPI;

public class HorizonCommandParser implements CommandExecutor
{
	FileConfiguration config;
	FileConfiguration data;
	ProfessionAPI prof;
	
	public HorizonCommandParser(FileConfiguration config, FileConfiguration data, ProfessionAPI prof)
	{
		this.config = config;
		this.data = data;
		this.prof = prof;
	}
	
    /**
     * onCommand() is called when a player enters a command recognised by Bukkit to belong to this plugin.
     * After that it is up to the contents of this method to determine what the commands do.
     * Returning false displays a command usage guide to the player.
     * 
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
	{
		//Main command
		if (commandLabel.equalsIgnoreCase("card"))
		{	
			//card
			if (args.length < 1)
				giveCommandGuide(sender);
				
			//card view
			else if (args[0].equalsIgnoreCase("view"))
				viewCard(sender);
			
			//card set
			else if (args[0].equalsIgnoreCase("set"))
			{	
				if (args.length < 2)
					givePossibleAttributes(sender);
				
				//card set name [name]
				else if (args[1].equalsIgnoreCase("name"))
					if (args.length >= 3)
						setName(sender, args);
					else
					{
						sender.sendMessage(ChatColor.GREEN + "Proper usage: /card set name [name]");
						sender.sendMessage(ChatColor.GREEN + "Enter a name under " + config.getInt("max name length") 
								+ " characters.");
					}
				
				//card set age [age]
				else if (args[1].equalsIgnoreCase("age"))
					if (args.length >= 3)
						setAge(sender, args[2]);
					else
					{
						sender.sendMessage(ChatColor.GREEN + "Proper usage: /card set age [age]");
						sender.sendMessage(ChatColor.GREEN + "Enter an age between " + config.getInt("min age") + " and " 
											+ config.getInt("max age") + ".");
					}
				
				//card set gender [gender]
				else if (args[1].equalsIgnoreCase("gender") || args[1].equalsIgnoreCase("sex"))
					if (args.length >= 3)
						setGender(sender, args[2]);
					else
					{
						sender.sendMessage(ChatColor.GREEN + "Proper usage: /card set gender [gender]");
						sender.sendMessage(ChatColor.GREEN + "Choose a gender from: " + config.getStringList("valid genders"));
					}
				
				//card set race [race]
				else if (args[1].equalsIgnoreCase("race"))
					if (args.length >= 3)
						setRace(sender, args[2]);
					else
					{
						sender.sendMessage(ChatColor.GREEN + "Proper usage: /card set race [race]");
						sender.sendMessage(ChatColor.GREEN + "Choose a race from: " + config.getStringList("valid races"));
					}
				
				//card set description [description]
				else if (args[1].equalsIgnoreCase("description"))
					if (args.length >= 3)
						setDescription(sender, args);
					else
					{
						sender.sendMessage(ChatColor.GREEN + "Proper usage: /card set description [description]");
						sender.sendMessage(ChatColor.GREEN + "Enter a description under " + config.getInt("max description length") 
											+ "characters.");
					}
				else
					givePossibleAttributes(sender);
			}
			
			//card create [name]
			else if (args[0].equalsIgnoreCase("create"))
				if (args.length >= 2)
					createCard(sender, args);
				else
				{
					sender.sendMessage(ChatColor.GREEN + "Proper usage: /card create [name]");
					sender.sendMessage(ChatColor.GREEN + "Enter the name of the new character card.");
				}
			//card delete [name]
			else if (args[0].equalsIgnoreCase("delete"))
				if (args.length >= 2)
					deleteCard(sender, args);
				else
				{
					sender.sendMessage(ChatColor.GREEN + "Proper usage: /card delete [name]");
					sender.sendMessage(ChatColor.GREEN + "Enter the name of the card you wish to delete.");
				}
			//card switch [name]
			else if (args[0].equalsIgnoreCase("switch"))
				if (args.length >= 2)
					switchCard(sender, args);
				else
				{
					sender.sendMessage(ChatColor.GREEN + "Proper usage: /card switch [name]");
					sender.sendMessage(ChatColor.GREEN + "Enter the name of the character card you would like to use.");
					sender.sendMessage(ChatColor.GREEN + "Cards: " + getCards(sender));
				}
			return true;
		}
		
		return false;
	}

	/**
	 * giveCommandGuide() displays a list of commands that the player has access to.
	 * 
	 * @param sender
	 */
	private void giveCommandGuide(CommandSender sender) 
	{
		if (!sender.hasPermission("horizoncards.help"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to view the command guide.");
			return;
		}
		
		sender.sendMessage("----------<" + ChatColor.DARK_GREEN + " Horizon Character Card Commands " + ChatColor.WHITE + ">----------");
		sender.sendMessage(ChatColor.DARK_GREEN + "Horizon Character Cards allows you to keep track of your character's attributes.");
		
		if (sender.hasPermission("horizoncards.view"))
		{
			sender.sendMessage(ChatColor.GREEN + "/card view");
			sender.sendMessage("View your character card.");
		}
		
		if (sender.hasPermission("horizoncards.set.name") || sender.hasPermission("horizoncards.set.age") 
				|| sender.hasPermission("horizoncards.set.gender") || sender.hasPermission("horizoncards.set.race")
				|| sender.hasPermission("horizoncards.set.description"))
		{
			sender.sendMessage(ChatColor.GREEN + "/card set");
			sender.sendMessage("Set an attribute in your character card.");
		}
		if (sender.hasPermission("horizoncards.create"))
		{
			sender.sendMessage(ChatColor.GREEN + "/card create [name]");
			sender.sendMessage("Create a new character card.");
		}
		if (sender.hasPermission("horizoncards.delete"))
		{
			sender.sendMessage(ChatColor.GREEN + "/card delete [name]");
			sender.sendMessage("Delete a character card.");
		}
		if (sender.hasPermission("horizoncards.switch"))
		{
			sender.sendMessage(ChatColor.GREEN + "/card switch [name");
			sender.sendMessage("Switch to a different character card.");
		}
	}
	
	/**
	 * viewCard() displays the sender's character card.
	 * 
	 * @param sender
	 */
	private void viewCard(CommandSender sender) 
	{
		if (!sender.hasPermission("horizoncards.view"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to view your character card.");
			return;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "That command can only be used by players.");
			return;
		}
		
		Player player = (Player) sender;
		new Card(config, data, player).view(prof, player);
	}
	
	/**
	 * setName() sets the sender's name in their character card.
	 * 
	 * @param sender
	 * @param name
	 */
	private void setName(CommandSender sender, String[] name)
	{
		if (!sender.hasPermission("horizoncards.set.name") || !sender.hasPermission("horizoncards.set"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to set your name.");
			return;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "That command can only be used by players.");
			return;
		}
		
		//Build the name - may be multiple words
		Player player = (Player) sender;
		StringBuilder builder = new StringBuilder();
		for (int i = 2; i < name.length; i++)
		{
		    builder.append(name[i]);
		    builder.append(" ");
		}
		if (builder.length() > 0)
			builder.setLength(builder.length() - 1);
			   
		//Set the name
		try { new Card(config, data, player).setName(builder.toString()); }
		catch (IllegalArgumentException e)
		{ 
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Name set to: " + builder.toString());
	}
	
	/**
	 * setAge() sets the sender's age in their character card.
	 * 
	 * @param sender
	 * @param age
	 */
	private void setAge(CommandSender sender, String age)
	{
		if (!sender.hasPermission("horizoncards.set.age") || !sender.hasPermission("horizoncards.set"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to set your age.");
			return;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "That command can only be used by players.");
			return;
		}
		
		Player player = (Player) sender;
		try { new Card(config, data, player).setAge(Integer.parseInt(age)); }
		catch (NumberFormatException e)
		{ 
			sender.sendMessage(ChatColor.RED + "That is not a valid number."); 
			return;
		}
		catch (IllegalArgumentException e)
		{ 
			sender.sendMessage(ChatColor.RED + e.getMessage()); 
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Age set to: " + age);
	}
	
	/**
	 * setGender() sets the sender's gender in their character card.
	 * 
	 * @param sender
	 * @param gender
	 */
	private void setGender(CommandSender sender, String gender)
	{
		if (!sender.hasPermission("horizoncards.set.gender") || !sender.hasPermission("horizoncards.set"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to set your gender.");
			return;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "That command can only be used by players.");
			return;
		}
		
		Player player = (Player) sender;
		try { new Card(config, data, player).setGender(gender); }
		catch (IllegalArgumentException e)
		{ 
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Gender set to: " + gender);
	}
	
	/**
	 * setRace() sets the sender's race in their character card.
	 * 
	 * @param sender
	 * @param race
	 */
	private void setRace(CommandSender sender, String race)
	{
		if (!sender.hasPermission("horizoncards.set.race") || !sender.hasPermission("horizoncards.set"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to set your race.");
			return;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "That command can only be used by players.");
			return;
		}
		
		Player player = (Player) sender;
		try { new Card(config, data, player).setRace(race); }
		catch (IllegalArgumentException e)
		{ 
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Race set to: " + race);
	}
	
	/**
	 * setDescription() sets the sender's description in their character card.
	 * 
	 * @param sender
	 * @param description
	 */
	private void setDescription(CommandSender sender, String[] description)
	{
		if (!sender.hasPermission("horizoncards.set.description") || !sender.hasPermission("horizoncards.set"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to set your description.");
			return;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "That command can only be used by players.");
			return;
		}
		
		Player player = (Player) sender;
		StringBuilder builder = new StringBuilder();
		for (int i = 2; i < description.length; i++)
		{
		    builder.append(description[i]);
		    builder.append(" ");
		}
		try { new Card(config, data, player).setDescription(builder.toString()); }
		catch (IllegalArgumentException e)
		{ 
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Description set to: " + builder.toString());
	}
	
	/**
	 * createCard() creates a new character card for the player and makes it he current card.
	 * 
	 * @param sender
	 * @param name
	 */
	private void createCard(CommandSender sender, String[] name)
	{
		if (!sender.hasPermission("horizoncards.create"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to create a new card.");
			return;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "That command can only be used by players.");
			return;
		}
		
		//Build the name - may be multiple words
		Player player = (Player) sender;
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < name.length; i++)
		{
		    builder.append(name[i]);
		    builder.append(" ");
		}
		if (builder.length() > 0)
			builder.setLength(builder.length() - 1);
			   
		//Set the name
		try { new Card(config, data, player).createCard(builder.toString()); }
		catch (IllegalArgumentException e)
		{ 
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Card created: " + builder.toString());
	}
	
	/**
	 * deleteCard() deletes the card specified.
	 * If it is the card currently in use, the card currently in use is changed to the next available card.
	 * 
	 * @param sender
	 * @param name
	 */
	private void deleteCard(CommandSender sender, String[] name)
	{
		if (!sender.hasPermission("horizoncards.delete"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to delete a card.");
			return;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "That command can only be used by players.");
			return;
		}
		
		//Build the name - may be multiple words
		Player player = (Player) sender;
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < name.length; i++)
		{
		    builder.append(name[i]);
		    builder.append(" ");
		}
		if (builder.length() > 0)
			builder.setLength(builder.length() - 1);
			   
		//Set the name
		try { new Card(config, data, player).deleteCard(builder.toString()); }
		catch (IllegalArgumentException e)
		{ 
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Card deleted: " + builder.toString());
	}
	
	/**
	 * switchCard() switches the current card of a player.
	 * 
	 * @param sender
	 * @param name
	 */
	private void switchCard(CommandSender sender, String[] name)
	{
		if (!sender.hasPermission("horizoncards.switch"))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to switch cards.");
			return;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "That command can only be used by players.");
			return;
		}
		
		//Build the name - may be multiple words
		Player player = (Player) sender;
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < name.length; i++)
		{
		    builder.append(name[i]);
		    builder.append(" ");
		}
		if (builder.length() > 0)
			builder.setLength(builder.length() - 1);
			   
		//Set the name
		try { new Card(config, data, player).switchCard(builder.toString()); }
		catch (IllegalArgumentException e)
		{ 
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Now using card: " + builder.toString());
	}
	
	/**
	 * getCards() returns a string containing all the cards that a player currently has.
	 * 
	 * @param sender
	 * @return
	 */
	private String getCards(CommandSender sender)
	{
		if (!(sender instanceof Player))
			return "None";
		
		Player player = (Player) sender;
		Set<String> cards;
		
		try 
		{ 
			cards = data.getConfigurationSection("cards." + player.getUniqueId()).getKeys(false);
			cards.remove("currentCard");
		} 
		catch (NullPointerException e)
		{ return "None"; }
		
		if (cards.isEmpty())
			return "None";
		
		//Build the name - may be multiple words
		StringBuilder builder = new StringBuilder();
		for (String card: cards)
		{
		    builder.append(card);
		    builder.append(", ");
		}
		if (builder.length() > 0)
			builder.setLength(builder.length() - 2);
		
		return builder.toString();
	}
	
	/**
	 * givePossibleAttributes() displays all the character card attributes that the sender is allowed to change.
	 * 
	 * @param sender
	 */
	private void givePossibleAttributes(CommandSender sender)
	{	
		if (sender instanceof Player && !(sender.hasPermission("horizoncards.set.name") || sender.hasPermission("horizoncards.set.age") 
				|| sender.hasPermission("horizoncards.set.gender") || sender.hasPermission("horizoncards.set.race")
				|| sender.hasPermission("horizoncards.set.description") || sender.hasPermission("horizoncards.set")))
		{
			sender.sendMessage(ChatColor.RED + "You don't have permission to set any attributes in your character card.");
			return;
		}
		
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "That command can only be used by players.");
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Set card attributes using /card set [attribute] [value]");
		
		String message = "Possible attributes:";
		
		if (sender.hasPermission("horizoncards.set.name"))
			message += " name";
		
		if (sender.hasPermission("horizoncards.set.age"))
			message += " age";
		
		if (sender.hasPermission("horizoncards.set.gender"))
			message += " gender";
		
		if (sender.hasPermission("horizoncards.set.race"))
			message += " race";
		
		if (sender.hasPermission("horizoncards.set.description"))
			message += " description";
		
		sender.sendMessage(ChatColor.GREEN + message);
	}
}
