package droidcon.sg.woodiemobile;

/**
 * Created by jeff on 1/4/18.
 */

public class RemoteCommand {
    String commandName;
    String commandValue;

    public RemoteCommand()
    {

    }

    public RemoteCommand(String commandName)
    {
        this.commandName = commandName;
    }

    public RemoteCommand(String commandName,String commandValue)
    {
        this.commandName = commandName;
        this.commandValue = commandValue;
    }

    public String getCommandName()
    {
        return commandName;
    }

    public String getCommandValue()
    {
        return commandValue;
    }
}
